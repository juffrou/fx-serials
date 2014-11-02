package org.juffrou.fx.seraials.dom;

import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;

import org.juffrou.fx.serials.FxSerialsBean;

public class TestPersonFX extends Person implements FxSerialsBean {

	public javafx.beans.property.adapter.JavaBeanIntegerProperty idProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder.create().bean(this).name("id").build(); }
	public javafx.beans.property.adapter.JavaBeanStringProperty nameProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanStringPropertyBuilder.create().bean(this).name("name").build(); }
	public javafx.beans.property.adapter.JavaBeanStringProperty emailProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanStringPropertyBuilder.create().bean(this).name("email").build(); }
	public javafx.beans.property.adapter.JavaBeanObjectProperty dateOfBirthProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder.create().bean(this).name("dateOfBirth").build(); }

	public ReadOnlyJavaBeanProperty getProperty(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
