package org.juffrou.fx.serials.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

public class FXInputStream extends ObjectInputStream {
	
	private static Set<Integer> SYSTEM_PACKAGES = new HashSet<Integer>();
	
	static {
		SYSTEM_PACKAGES.add("java".hashCode());
		SYSTEM_PACKAGES.add("javax".hashCode());
		SYSTEM_PACKAGES.add("jdk".hashCode());
		SYSTEM_PACKAGES.add("sun".hashCode());
	}
	
	private ClassPool pool = ClassPool.getDefault();

	public FXInputStream() throws IOException, SecurityException {
		super();
	}

	public FXInputStream(InputStream in) throws IOException {
		super(in);
	}

	@Override
	protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException,	ClassNotFoundException {
		try {
			String name = desc.getName();
			int i = name.indexOf('.');
			if(i != -1 && SYSTEM_PACKAGES.contains(name.substring(0,i).hashCode()))
				return super.resolveClass(desc);
			i = name.lastIndexOf('.');
			String pck = (i == -1 ? "fx_." : name.substring(0, i) + "._fx_.");
			name = name.substring(i + 1);
			CtClass ctClass = pool.makeClass(pck + name);
	        // add the same serialVersionUID as the base class
	        CtField field = new CtField(CtClass.longType, "serialVersionUID", ctClass);
	        field.setModifiers(Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL);
	        ctClass.addField(field, desc.getSerialVersionUID() + "L");
	        ctClass.setSuperclass(pool.get(desc.getName()));
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
		if(type.isPrimitive())
			simpleName = Character.valueOf((char) (simpleName.charAt(0) - 32)) + simpleName.substring(1);

		if(simpleName.equals("String")) {
			info.returnType = "javafx.beans.property.adapter.JavaBeanStringProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanStringPropertyBuilder";
		}
		else if(simpleName.equals("Integer")) {
			info.returnType = "javafx.beans.property.adapter.JavaBeanIntegerProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder";
		}
		else if(simpleName.equals("Long")) {
			info.returnType = "javafx.beans.property.adapter.JavaBeanLongProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanLongPropertyBuilder";
		}
		else if(simpleName.equals("Boolean")) {
			info.returnType = "javafx.beans.property.adapter.JavaBeanBooleanProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanBooleanPropertyBuilder";
		}
		else if(simpleName.equals("Double")) {
			info.returnType = "javafx.beans.property.adapter.JavaBeanDoubleProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanDoublePropertyBuilder";
		}
		else if(simpleName.equals("Float")) {
			info.returnType = "javafx.beans.property.adapter.JavaBeanFloatProperty";
			info.builder = "javafx.beans.property.adapter.JavaBeanFloatPropertyBuilder";
		}
		else {
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

