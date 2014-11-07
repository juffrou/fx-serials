package org.juffrou.fx.serials.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtField.Initializer;
import javassist.CtMethod;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.juffrou.fx.serials.error.FxSerialsProxyAlreadExistsException;
import org.juffrou.fx.serials.error.FxSerialsProxyCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates Java Classes at runtime.<p/>
 * The created classes will extend a traditional Java Bean and implement the JavaFX Bean specification
 * by adding methods to obtain JavaFX2 properties corresponding to the traditional Java Bean Properties.
 * 
 * @author Carlos Martins
 *
 */
public class FxSerialsProxyBuilder {
	
	private static final Logger logger = LoggerFactory.getLogger(FxSerialsProxyBuilder.class);

	private static final int HASH_STRING = -1808118735;
	private static final int HASH_INTEGER = -672261858;
	private static final int HASH_LONG = 2374300;
	private static final int HASH_BOOLEAN = 1729365000;
	private static final int HASH_DOUBLE = 2052876273;
	private static final int HASH_FLOAT = 67973692;
	
	private final ClassPool pool;
	

	public FxSerialsProxyBuilder() {
		this(ClassPool.getDefault());
	}

	public FxSerialsProxyBuilder(ClassPool pool) {
		this.pool = pool;
	}

	/**
	 * Collects information about bean property fields declared in the class and its super classes
	 * @param fields List to collect information into
	 * @param clazz class to analyze.
	 */
	private void collectFieldInfo(List<FieldInfo> fields, Class<?> clazz) {
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != Object.class) {
			collectFieldInfo(fields, superclass);
		}
		for (Field f : clazz.getDeclaredFields()) {
			if(!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				Class<?> type = f.getType();
				
				String getter = inspectReadMethod(clazz, f.getName(), type);
				String setter = inspectWriteMethod(clazz, f.getName(), type);
				
				if(getter == null)
					continue; // A property with no getter is ignored

				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.field = f;
				fieldInfo.getter = getter;
				fieldInfo.setter = setter;

				String simpleName = type.getSimpleName();
				if(type.isPrimitive()) {
					if(simpleName.equals("int"))
						simpleName = "Integer";
					else
						simpleName = Character.valueOf((char) (simpleName.charAt(0) - 32)) + simpleName.substring(1);
				}
				
				switch(simpleName.hashCode()) {
				case HASH_STRING:
					if(setter != null) {
						fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanStringProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanStringPropertyBuilder";
					}
					else {
						// property is readonly
						fieldInfo.returnType = "javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder";
					}
					break;
				case HASH_INTEGER:
					if(setter != null) {
						fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanIntegerProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder";
					}
					else {
						fieldInfo.returnType = "javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerPropertyBuilder";
					}
					break;
				case HASH_LONG:
					if(setter != null) {
						fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanLongProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanLongPropertyBuilder";
					}
					else {
						fieldInfo.returnType = "javafx.beans.property.adapter.ReadOnlyJavaBeanLongProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.ReadOnlyJavaBeanLongPropertyBuilder";
					}
					break;
				case HASH_BOOLEAN:
					if(setter != null) {
						fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanBooleanProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder";
					}
					else {
						fieldInfo.returnType = "javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.ReadOnlyJavaBeanBooleanPropertyBuilder";
					}
					break;
				case HASH_DOUBLE:
					if(setter != null) {
						fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanDoubleProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder";
					}
					else {
						fieldInfo.returnType = "javafx.beans.property.adapter.ReadOnlyJavaBeanDoubleProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.ReadOnlyJavaBeanDoublePropertyBuilder";
					}
					break;
				case HASH_FLOAT:
					if(setter != null) {
						fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanFloatProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder";
					}
					else {
						fieldInfo.returnType = "javafx.beans.property.adapter.ReadOnlyJavaBeanFloatProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.ReadOnlyJavaBeanFloatPropertyBuilder";
					}
					break;
				default:
					if(setter != null) {
						fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanObjectProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder";
					}
					else {
						fieldInfo.returnType = "javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty";
						fieldInfo.builder = "javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder";
					}
				}
				
				
				fields.add(fieldInfo);
			}
		}
	}

	/**
	 * Find the getter method of one property.
	 * @param beanClass
	 * @param fieldName
	 * @param fieldClass
	 * @return
	 */
	private String inspectReadMethod(Class<?> beanClass, String fieldName, Class<?> fieldClass) {
		Method getterMethod;
		String name = fieldName;
		String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
		try {
			getterMethod = beanClass.getMethod(methodName, null);
			return getterMethod.getName();
		} catch (NoSuchMethodException e) {
			
			// try the boolean "is" pattern
			if(fieldClass == boolean.class || fieldClass == null) {
				if(name.startsWith("is"))
					name = name.substring(2);
				methodName = "is" + name.substring(0, 1).toUpperCase() + name.substring(1);
				try {
					getterMethod = beanClass.getMethod(methodName, null);
					return getterMethod.getName();
				} catch (NoSuchMethodException e1) {
					return null;
				}
			}
			else
				return null;

		}
	}

	/**
	 * Find the setter method of one property.
	 * @param beanClass
	 * @param fieldName
	 * @param fieldClass
	 * @return
	 */
	private String inspectWriteMethod(Class<?> beanClass, String fieldName, Class<?> fieldClass) {
		String name = fieldName;
		String methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
		try {

			return beanClass.getMethod(methodName, fieldClass).getName();

		} catch (NoSuchMethodException e) {
			
			// try the boolean "is" pattern
			if(fieldClass == boolean.class) {
				if(name.startsWith("is"))
					name = name.substring(2);
				methodName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1);
				try {
					return beanClass.getMethod(methodName, fieldClass).getName();
				} catch (NoSuchMethodException e1) {
					return null;
				}
			}
			else
				return null;
		}
	}

	/**
	 * Creates a proxy based on a specific class.<br>
	 * The proxy will extend the specified class and implement the FxSerialsProxy interface.<br>
	 * Setter methods will be overriden to notify the property of a value change.
	 * @param fxSerials class to proxy
	 * @param svUID serialVersionUID field value of the class to proxy
	 * @return the proxy class.
	 * @throws FxSerialsProxyAlreadExistsException
	 */
	public Class<?> buildFXSerialsProxy(Class<?> fxSerials, long svUID) throws FxSerialsProxyAlreadExistsException {
		
		try {
			List<FieldInfo> fields = new ArrayList<FieldInfo>();
			collectFieldInfo(fields, fxSerials);
			String name = fxSerials.getName();
			int i = name.lastIndexOf('.');
			String pck = (i == -1 ? "fx_." : name.substring(0, i) + "._fx_.");
			name = pck + name.substring(i + 1);
			CtClass ctClass = null;
			try {
				
				ctClass = pool.getOrNull(name);
				if(ctClass != null) {

					if(logger.isDebugEnabled())
						logger.debug("Found existing proxy " + name);
					
					return Class.forName(name);
				}

				if(logger.isDebugEnabled())
					logger.debug("Creating proxy " + name + " with serialVersionUID=" + svUID);

				ctClass = pool.makeClass(name);
				
			} catch(RuntimeException e) {
				throw new FxSerialsProxyAlreadExistsException();
			} catch (ClassNotFoundException e) {
				throw new FxSerialsProxyCreationException("Proxy already exists, but I cannot do Class.forName() on it", e);
			}
			
	        // add the same serialVersionUID as the base class so that the deserializer does not complain
	        CtField field = new CtField(CtClass.longType, "serialVersionUID", ctClass);
	        field.setModifiers(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
	        ctClass.addField(field, Initializer.constant(svUID));
	        
	        // add serializable interface
	        ctClass.addInterface(pool.get("java.io.Serializable"));
	        
	        // add initialized properties map
	        CtClass hashMapClass = pool.get("java.util.HashMap");
	        CtField fxProperties = new CtField(hashMapClass, "fxProperties", ctClass);
	        fxProperties.setModifiers(Modifier.PRIVATE);
	        ctClass.addField(fxProperties, Initializer.byExpr("new java.util.HashMap();"));
	        
	        // add constructor
	        CtConstructor defaultConstructor = CtNewConstructor.defaultConstructor(ctClass);
	        defaultConstructor.setBody("{super();}");
	        ctClass.addConstructor(defaultConstructor);
	        
	        // add a method for FxInputStream to initialize the properties list
	        CtMethod initMethod = CtNewMethod.make("public void initPropertiesList() {this.fxProperties = new java.util.HashMap();}", ctClass);
	        ctClass.addMethod(initMethod);
	        
	        // implement FxSerialsProxy
	        implementFxSerialsProxy(ctClass);

	        // extend FxSerials
	        ctClass.setSuperclass(pool.get(fxSerials.getName()));
	        
	        // add methods for each property
	        addPropertyMethods(ctClass, fields);
	        
			Class<?> proxyClass = ctClass.toClass();
			
			return proxyClass;
			
		} catch (NotFoundException | CannotCompileException e) {
			throw new FxSerialsProxyCreationException("Error creating FxSerialsProxy for class "+fxSerials.getName()+ ": "+e.getMessage(), e);
		}
	}
	
	/**
	 * Adds the methods defined in the interface FxSerialsProxy and adds the implements declaration
	 * @param ctClass
	 * @throws CannotCompileException
	 * @throws NotFoundException
	 */
	private void implementFxSerialsProxy(CtClass ctClass) throws CannotCompileException, NotFoundException {
        // implement FxSerialsProxy
        CtMethod getPropertyMethod = CtNewMethod.make(
        		"public javafx.beans.property.adapter.ReadOnlyJavaBeanProperty getProperty(String propertyName) {"
		+"try {"+
			"java.lang.reflect.Method m = getClass().getMethod(propertyName + \"Property\", null);"+
			"javafx.beans.property.adapter.ReadOnlyJavaBeanProperty p = (javafx.beans.property.adapter.ReadOnlyJavaBeanProperty) m.invoke(this, null);"+
			"return p;"+
		"} catch (NoSuchMethodException e) {"+
			"throw new org.juffrou.fx.serials.error.PropertyMethodException(\"Error invoking \"+propertyName+\"Property method (NoSuchMethod): \" + e.getMessage(), e);"+
		"} catch (SecurityException e) {"+
			"throw new org.juffrou.fx.serials.error.PropertyMethodException(\"Error invoking \"+propertyName+\"Property method (SecurityException): \" + e.getMessage(), e);"+
		"} catch (IllegalAccessException e) {"+
			"throw new org.juffrou.fx.serials.error.PropertyMethodException(\"Error invoking \"+propertyName+\"Property method (IllegalAccess): \" + e.getMessage(), e);"+
		"} catch (IllegalArgumentException e) {"+
			"throw new org.juffrou.fx.serials.error.PropertyMethodException(\"Error invoking \"+propertyName+\"Property method (IllegalArgument): \" + e.getMessage(), e);"+
		"} catch (java.lang.reflect.InvocationTargetException e) {"+
			"throw new org.juffrou.fx.serials.error.PropertyMethodException(\"Error invoking \"+propertyName+\"Property method (InvocationTargetException): \" + e.getMessage(), e);"+
		"} }"
        , ctClass);
        ctClass.addMethod(getPropertyMethod);
        ctClass.addInterface(pool.get("org.juffrou.fx.serials.FxSerialsProxy"));
	}
	
	private void addPropertyMethods(CtClass ctClass, List<FieldInfo> fields) throws NotFoundException, CannotCompileException {
		for (FieldInfo fieldInfo : fields) {
			
			// build property method
			String name = fieldInfo.field.getName();
			StringBuilder methodBody = new StringBuilder();
			methodBody.append("public " + fieldInfo.returnType + " " + name + "Property() {");
			methodBody.append(fieldInfo.returnType + " p = ("+fieldInfo.returnType+") this.fxProperties.get(\""+name+"\");");
			methodBody.append("if(p == null) { try {");
			methodBody.append("p = "+fieldInfo.builder+".create().bean(this).name(\""+name+"\").getter(\""+fieldInfo.getter+"\")");
			if(fieldInfo.setter != null)
				methodBody.append(".setter(\""+fieldInfo.setter+"\")");
			methodBody.append(".build();");
			methodBody.append("this.fxProperties.put(\""+name+"\", p);");
			methodBody.append("} catch (NoSuchMethodException e) {throw new org.juffrou.fx.serials.error.FxPropertyCreationException(\"Error creating FxProperty for bean property + "+name+"\", e);}");
			methodBody.append("} return p; }");
			CtMethod m = CtNewMethod.make(methodBody.toString(), ctClass);
			ctClass.addMethod(m);
			
			if(fieldInfo.setter != null) {
				// override setter method
				methodBody.setLength(0);
				methodBody.append("public void " + fieldInfo.setter+ "(" + fieldInfo.field.getType().getName() + " value) {");
				methodBody.append("super." + fieldInfo.setter + "(value);");
				methodBody.append(name + "Property().fireValueChangedEvent();");
				methodBody.append("}");
				m = CtNewMethod.make(methodBody.toString(), ctClass);
				ctClass.addMethod(m);
			}
		}
	}
	
	private class FieldInfo {
		public Field field;
		public String getter;
		public String setter;
		public String returnType;
		public String builder;
	}

}
