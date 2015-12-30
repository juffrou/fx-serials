package org.juffrou.fx.seraials.dom;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;

public class TestPersonFX extends Person implements
		org.juffrou.fx.serials.JFXProxy {

	private java.util.Map __fx_properties = new java.util.HashMap();

	public javafx.beans.property.adapter.JavaBeanIntegerProperty idProperty() {
		if (__fx_properties == null)
			throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
					"__fx_properties not initialized");
		javafx.beans.property.adapter.JavaBeanIntegerProperty p = (javafx.beans.property.adapter.JavaBeanIntegerProperty) __fx_properties
				.get("id");
		if (p == null) {
			try {
				p = javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder
						.create().bean(this).name("id").build();
				__fx_properties.put("id", p);
			} catch (NoSuchMethodException e) {
				throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
						"Error creating FxProperty for bean property + id", e);
			}
		}
		return p;
	}

	public javafx.beans.property.adapter.JavaBeanStringProperty nameProperty() {
		if (__fx_properties == null)
			throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
					"__fx_properties not initialized");
		javafx.beans.property.adapter.JavaBeanStringProperty p = (javafx.beans.property.adapter.JavaBeanStringProperty) __fx_properties
				.get("name");
		if (p == null) {
			try {
				p = javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
						.create().bean(this).name("name").build();
				__fx_properties.put("name", p);
			} catch (NoSuchMethodException e) {
				throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
						"Error creating FxProperty for bean property + name", e);
			}
		}
		return p;
	}

	public javafx.beans.property.adapter.JavaBeanStringProperty emailProperty() {
		if (__fx_properties == null)
			throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
					"__fx_properties not initialized");
		javafx.beans.property.adapter.JavaBeanStringProperty p = (javafx.beans.property.adapter.JavaBeanStringProperty) __fx_properties
				.get("email");
		if (p == null) {
			try {
				p = javafx.beans.property.adapter.JavaBeanStringPropertyBuilder
						.create().bean(this).name("email").build();
				__fx_properties.put("email", p);
			} catch (NoSuchMethodException e) {
				throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
						"Error creating FxProperty for bean property + email",
						e);
			}
		}
		return p;
	}

	public javafx.beans.property.adapter.JavaBeanObjectProperty dateOfBirthProperty() {
		if (__fx_properties == null)
			throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
					"__fx_properties not initialized");
		javafx.beans.property.adapter.JavaBeanObjectProperty p = (javafx.beans.property.adapter.JavaBeanObjectProperty) __fx_properties
				.get("dateOfBirth");
		if (p == null) {
			try {
				p = javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder
						.create().bean(this).name("dateOfBirth").build();
				__fx_properties.put("dateOfBirth", p);
			} catch (NoSuchMethodException e) {
				throw new org.juffrou.fx.serials.error.FxPropertyCreationException(
						"Error creating FxProperty for bean property + dateOfBirth",
						e);
			}
		}
		return p;
	}

	public javafx.beans.property.adapter.ReadOnlyJavaBeanProperty getProperty(
			String propertyName) {
		try {
			Method m = getClass().getMethod(propertyName + "Property", null);
			return (ReadOnlyJavaBeanProperty) m.invoke(this, null);
		} catch (NoSuchMethodException e) {
			throw new org.juffrou.fx.serials.error.PropertyMethodException(
					"Error invoking " + propertyName + "Property method", e);
		} catch (SecurityException e) {
			throw new org.juffrou.fx.serials.error.PropertyMethodException(
					"Error invoking " + propertyName + "Property method", e);
		} catch (IllegalAccessException e) {
			throw new org.juffrou.fx.serials.error.PropertyMethodException(
					"Error invoking " + propertyName + "Property method", e);
		} catch (IllegalArgumentException e) {
			throw new org.juffrou.fx.serials.error.PropertyMethodException(
					"Error invoking " + propertyName + "Property method", e);
		} catch (InvocationTargetException e) {
			throw new org.juffrou.fx.serials.error.PropertyMethodException(
					"Error invoking " + propertyName + "Property method", e);
		}
	}

}
