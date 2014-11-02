package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.juffrou.fx.serials.FxSerials;
import org.juffrou.fx.serials.FxSerialsBean;
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
		if( FxSerials.class.isAssignableFrom(resolveClass) && ! FxSerialsBean.class.isAssignableFrom(resolveClass) )
			try {
				resolveClass = buildFXSerialsBean(resolveClass, desc.getSerialVersionUID());
			} catch (FxSerialsBeanAlreadExistsException e) { }
		return resolveClass;
	}
	
	private Class<?> buildFXSerialsBean(Class<?> fxSerials, long svUID) throws FxSerialsBeanAlreadExistsException {

		try {
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
	        addPropertyMethods(ctClass);
			Class<?> resolveClass = ctClass.toClass();
			return resolveClass;
		} catch (NotFoundException | CannotCompileException e) {
			throw new FxSerialsBeanCreationException("Error creating FxSerialsBean for class "+fxSerials.getName()+ ": "+e.getMessage(), e);
		}
		
	}
	
	private void addPropertyMethods(CtClass ctClass) throws NotFoundException, CannotCompileException {
		CtClass superclass = ctClass.getSuperclass();
		CtField[] declaredFields = superclass.getDeclaredFields();
		for (CtField ctField : declaredFields) {
			if((ctField.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
				continue;
			FXInfo info = getFXInfo(ctField);
			String name = ctField.getName();
			String methodBody =
					"public " + info.returnType + " " + name + "Property() {"+
//							"System.out.println(\"Entered method\");"+
//					        "if(this.fxProperties == null) this.fxProperties = new java.util.HashMap();"+
//							"System.out.println(\"fxProperties size=\");"+
							info.returnType + " p = ("+info.returnType+") this.fxProperties.get(\""+name+"\");"+
							"if(p == null) { try {"+
							"p = "+info.builder+".create().bean(this).name(\""+name+"\").build();"+
							"this.fxProperties.put(\""+name+"\", p);"+
							"} catch (NoSuchMethodException e) {throw new org.juffrou.fx.serials.error.FxPropertyCreationException(\"Error creating FxProperty for bean property + "+name+"\", e);}"+
							"} return p; }";
//			System.out.println(methodBody);
			CtMethod m = CtNewMethod.make(methodBody, ctClass);
			ctClass.addMethod(m);
		}
	}
	
	private FXInfo getFXInfo(CtField ctField) throws NotFoundException {
		FXInfo info = new FXInfo();
		CtClass type = ctField.getType();
		String simpleName = type.getSimpleName();
		if(type.isPrimitive()) {
			if(simpleName.equals("int"))
				simpleName = "Integer";
			else
				simpleName = Character.valueOf((char) (simpleName.charAt(0) - 32)) + simpleName.substring(1);
		}
		
		switch(simpleName.hashCode()) {
		case HASH_STRING:
			info.returnType = "javafx.beans.property.adapter.JavaBeanStringProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanStringPropertyBuilder";
			break;
		case HASH_INTEGER:
			info.returnType = "javafx.beans.property.adapter.JavaBeanIntegerProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder";
			break;
		case HASH_LONG:
			info.returnType = "javafx.beans.property.adapter.JavaBeanLongProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanLongPropertyBuilder";
			break;
		case HASH_BOOLEAN:
			info.returnType = "javafx.beans.property.adapter.JavaBeanBooleanProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder";
			break;
		case HASH_DOUBLE:
			info.returnType = "javafx.beans.property.adapter.JavaBeanDoubleProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder";
			break;
		case HASH_FLOAT:
			info.returnType = "javafx.beans.property.adapter.JavaBeanFloatProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder";
			break;
		default:
			info.returnType = "javafx.beans.property.adapter.JavaBeanObjectProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder";
		}

		return info;
	}
	
	private class FXInfo {
		public String returnType;
		public String builder;
	}
}

