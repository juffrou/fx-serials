package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.juffrou.fx.serials.FxSerials;
import org.juffrou.fx.serials.error.FxSerialsBeanAlreadExistsException;
import org.juffrou.fx.serials.error.FxSerialsBeanCreationException;

/**
 * Deserializes traditional Java Beans into JavaFX2 Beans.<br>
 * The source Java Beans must implement FxSerials.
 * 
 * @author Carlos Martins
 */
public class FxInputStream extends ObjectInputStream {

	private static final int HASH_STRING = -1808118735;
	private static final int HASH_INTEGER = -672261858;
	private static final int HASH_LONG = 2374300;
	private static final int HASH_BOOLEAN = 1729365000;
	private static final int HASH_DOUBLE = 2052876273;
	private static final int HASH_FLOAT = 67973692;
	
	private final ClassPool pool;

	protected FxInputStream() throws IOException, SecurityException {
		super();
		pool = ClassPool.getDefault();
	}

	public FxInputStream(InputStream in) throws IOException {
		this(in, ClassPool.getDefault());
	}

	public FxInputStream(InputStream in, ClassPool pool) throws IOException {
		super(in);
		this.pool = pool;
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,	ClassNotFoundException {
		
		Class<?> resolveClass = super.resolveClass(desc);
		if( implementsFxSerials(resolveClass) )
			try {
				resolveClass = buildFXSerialsBean(resolveClass, desc.getSerialVersionUID());
			} catch (FxSerialsBeanAlreadExistsException e) { }
		return resolveClass;
	}
	
	/**
	 * Test if a class declares to implement the interface FxSerials
	 * 
	 * @param clazz
	 *            class to test
	 * @return true if the class declares FxSerials implementation
	 */
	private boolean implementsFxSerials(Class<?> clazz) {
		for (Class<?> itf : clazz.getInterfaces())
			if (itf == FxSerials.class)
				return true;
		return false;
	}
	
	/**
	 * Collects information about bean property fields declared in the class and its super classes
	 * @param fields
	 * @param clazz
	 */
	private void collectFieldInfo(List<FieldInfo> fields, Class<?> clazz) {
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != Object.class) {
			collectFieldInfo(fields, superclass);
		}
		for (Field f : clazz.getDeclaredFields()) {
			if(!Modifier.isStatic(f.getModifiers()) && !Modifier.isTransient(f.getModifiers())) {
				FieldInfo fieldInfo = new FieldInfo();
				fieldInfo.field = f;
				Class<?> type = f.getType();
				String simpleName = type.getSimpleName();
				if(type.isPrimitive()) {
					if(simpleName.equals("int"))
						simpleName = "Integer";
					else
						simpleName = Character.valueOf((char) (simpleName.charAt(0) - 32)) + simpleName.substring(1);
				}
				
				switch(simpleName.hashCode()) {
				case HASH_STRING:
					fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanStringProperty";
					fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanStringPropertyBuilder";
					break;
				case HASH_INTEGER:
					fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanIntegerProperty";
					fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder";
					break;
				case HASH_LONG:
					fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanLongProperty";
					fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanLongPropertyBuilder";
					break;
				case HASH_BOOLEAN:
					fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanBooleanProperty";
					fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder";
					break;
				case HASH_DOUBLE:
					fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanDoubleProperty";
					fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder";
					break;
				case HASH_FLOAT:
					fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanFloatProperty";
					fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder";
					break;
				default:
					fieldInfo.returnType = "javafx.beans.property.adapter.JavaBeanObjectProperty";
					fieldInfo.builder = "javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder";
				}
				
				
				fields.add(fieldInfo);
			}
		}
	}

	private Class<?> buildFXSerialsBean(Class<?> fxSerials, long svUID) throws FxSerialsBeanAlreadExistsException {

		try {
			List<FieldInfo> fields = new ArrayList<FieldInfo>();
			collectFieldInfo(fields, fxSerials);
			String name = fxSerials.getName();
			int i = name.lastIndexOf('.');
			String pck = (i == -1 ? "fx_." : name.substring(0, i) + "._fx_.");
			name = name.substring(i + 1);
			CtClass ctClass = null;
			try {
				ctClass = pool.makeClass(pck + name);
			} catch(RuntimeException e) {
				throw new FxSerialsBeanAlreadExistsException();
			}
	        // add the same serialVersionUID as the base class
	        CtField field = new CtField(CtClass.longType, "serialVersionUID", ctClass);
	        field.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
	        ctClass.addField(field, svUID + "L");
	        // add initialized properties map
	        CtClass hashMapClass = pool.get("java.util.HashMap");
	        CtField fxProperties = new CtField(hashMapClass, "fxProperties", ctClass);
//	        CtField fxProperties = CtField.make("private final java.util.Map fxProperties = new java.util.HashMap();", ctClass);
	        ctClass.addField(fxProperties, CtField.Initializer.byNew(hashMapClass));
	        // add constructor
//	        CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
//	        ctConstructor.setBody("this.fxProperties = new java.util.HashMap();");
//	        ctClass.addConstructor(ctConstructor);
	        // implement FxSerialsBean
	        CtMethod getPropertyMethod = CtNewMethod.make(
	        		"public javafx.beans.property.adapter.ReadOnlyJavaBeanProperty getProperty(String propertyName) {"
			+"try {"+
				"java.lang.reflect.Method m = getClass().getMethod(propertyName + \"Property\", null);"+
//				"System.out.println(\"calling method: \"+m);"+
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
	        ctClass.addInterface(pool.get("org.juffrou.fx.serials.FxSerialsBean"));
	        // extend FxSerials
	        ctClass.setSuperclass(pool.get(fxSerials.getName()));
	        addPropertyMethods(ctClass, fields);
			Class<?> resolveClass = ctClass.toClass();
			return resolveClass;
		} catch (NotFoundException | CannotCompileException e) {
			throw new FxSerialsBeanCreationException("Error creating FxSerialsBean for class "+fxSerials.getName()+ ": "+e.getMessage(), e);
		}
		
	}
	
	private void addPropertyMethods(CtClass ctClass, List<FieldInfo> fields) throws NotFoundException, CannotCompileException {
		for (FieldInfo fieldInfo : fields) {
			String name = fieldInfo.field.getName();
			String methodBody =
					"public " + fieldInfo.returnType + " " + name + "Property() {"+
//							"System.out.println(\"Entered method\");"+
					        "if(this.fxProperties == null) this.fxProperties = new java.util.HashMap();"+
//							"System.out.println(\"fxProperties size=\");"+
							fieldInfo.returnType + " p = ("+fieldInfo.returnType+") this.fxProperties.get(\""+name+"\");"+
							"if(p == null) { try {"+
							"p = "+fieldInfo.builder+".create().bean(this).name(\""+name+"\").build();"+
							"this.fxProperties.put(\""+name+"\", p);"+
							"} catch (NoSuchMethodException e) {throw new org.juffrou.fx.serials.error.FxPropertyCreationException(\"Error creating FxProperty for bean property + "+name+"\", e);}"+
							"} return p; }";
//			System.out.println(methodBody);
			CtMethod m = CtNewMethod.make(methodBody, ctClass);
			ctClass.addMethod(m);
		}
	}
	
	private class FieldInfo {
		public Field field;
		public String returnType;
		public String builder;
	}
}

