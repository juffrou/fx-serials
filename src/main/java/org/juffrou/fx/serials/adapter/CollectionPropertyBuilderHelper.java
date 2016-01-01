package org.juffrou.fx.serials.adapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.juffrou.fx.serials.error.FxPropertyCreationException;

public final class CollectionPropertyBuilderHelper {
	
	private Object bean;
	private String name;
	private String getterName;
	private String setterName;

	CollectionPropertyBuilderHelper() {}

	public Object getBean() {
		return bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGetterName() {
		return getterName;
	}

	public void setGetterName(String getterName) {
		this.getterName = getterName;
	}

	public String getSetterName() {
		return setterName;
	}

	public void setSetterName(String setterName) {
		this.setterName = setterName;
	}
	
	public Object getCollection() {
		
		try {
			Method m = bean.getClass().getMethod(getterName, null);
			Object collection = m.invoke(bean, null);
			return collection;
		} catch (NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			throw new FxPropertyCreationException("Cannot find method " + getterName, e);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			throw new FxPropertyCreationException("Error invoking method " + getterName, e);
		}
		
	}

}
