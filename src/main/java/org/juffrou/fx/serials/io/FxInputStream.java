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

public class FxInputStream extends ObjectInputStream {

	private static final int HASH_STRING = -1808118735;
	private static final int HASH_INTEGER = -672261858;
	private static final int HASH_LONG = 2374300;
	private static final int HASH_BOOLEAN = 1729365000;
	private static final int HASH_DOUBLE = 2052876273;
	private static final int HASH_FLOAT = 67973692;
	
	private ClassPool pool = ClassPool.getDefault();

	public FxInputStream() throws IOException, SecurityException {
		super();
	}

	public FxInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,	ClassNotFoundException {
		
		Class<?> resolveClass = super.resolveClass(desc);
		if( FxSerials.class.isAssignableFrom(resolveClass) && ! FxSerialsBean.class.isAssignableFrom(resolveClass) )
			resolveClass = buildFXSerialsBean(resolveClass, desc.getSerialVersionUID());
		return resolveClass;
	}
	
	private Class<?> buildFXSerialsBean(Class<?> fxSerials, long svUID) {

		try {
			String name = fxSerials.getName();
			int i = name.lastIndexOf('.');
			String pck = (i == -1 ? "fx_." : name.substring(0, i) + "._fx_.");
			name = name.substring(i + 1);
			CtClass ctClass = pool.makeClass(pck + name);
	        // add the same serialVersionUID as the base class
	        CtField field = new CtField(CtClass.longType, "serialVersionUID", ctClass);
	        field.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
	        ctClass.addField(field, svUID + "L");
	        ctClass.setSuperclass(pool.get(fxSerials.getName()));
	        addPropertyMethods(ctClass);
			Class<?> resolveClass = ctClass.toClass();
			return resolveClass;
		} catch (NotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CannotCompileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	private void addPropertyMethods(CtClass ctClass) throws NotFoundException, CannotCompileException {
		CtClass superclass = ctClass.getSuperclass();
		CtField[] declaredFields = superclass.getDeclaredFields();
		for (CtField ctField : declaredFields) {
			if((ctField.getModifiers() & Modifier.STATIC) == Modifier.STATIC)
				continue;
			FXInfo info = getFXInfo(ctField);
			String name = ctField.getName();
			CtMethod m = CtNewMethod.make(
	                 "public " + info.returnType + " " + name + "Property() throws java.lang.NoSuchMethodException { return "+info.builder+".create().bean(this).name(\""+name+"\").build(); }",
	                 ctClass);
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

