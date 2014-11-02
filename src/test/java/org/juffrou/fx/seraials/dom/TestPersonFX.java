package org.juffrou.fx.seraials.dom;

public class TestPersonFX extends Person {

	public javafx.beans.property.adapter.JavaBeanIntegerProperty idProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanIntegerPropertyBuilder.create().bean(this).name("id").build(); }
	public javafx.beans.property.adapter.JavaBeanStringProperty nameProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanStringPropertyBuilder.create().bean(this).name("name").build(); }
	public javafx.beans.property.adapter.JavaBeanStringProperty emailProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanStringPropertyBuilder.create().bean(this).name("email").build(); }
	public javafx.beans.property.adapter.JavaBeanObjectProperty dateOfBirthProperty() throws java.lang.NoSuchMethodException { return javafx.beans.property.adapter.JavaBeanObjectPropertyBuilder.create().bean(this).name("dateOfBirth").build(); }
	
}
