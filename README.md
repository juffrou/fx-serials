Juffrou FXSerials
=================

_Automagical JavaFX2 Beans_

Copyright (C) 2015- by Carlos Martins, All rights reserved.

This library allows you to transform traditional Java Beans into JavaFX2 Beans by adding property methods which return the appropriate JavaFX2 property type for each bean attribute. The transformed JavaFX2 beans can later on by transformed back into their original Java Beans.

Given a traditional java bean with a `name` attribute like the following:

```java
	
	package example.fxseraials

	public class Person implements JFXSerializable {
	
		private static final long serialVersionUID = 6329998877045393661L;

		private String name;
	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}
```

Its corresponding JavaFX2 Bean will extend it and add a `JavaBeanStringProperty` for the `name` attribute called nameProperty. If it were written in Java source code it would look like the following:

```java

	package example.fxseraials._fx_

	public class Person extends example.fxseraials.Person implements JFXProxy {

		private static final long serialVersionUID = 6329998877045393661L;

		private Map<String, JavaBeanStringProperty> __fx_properties = new HashMap<String, ReadOnlyJavaBeanProperty>();

		public ReadOnlyJavaBeanProperty getProperty(String propertyName) {
			Method m = getClass().getMethod(propertyName + "Property", null);
			return (ReadOnlyJavaBeanProperty) m.invoke(this, null);
		}
		
		public JavaBeanStringProperty nameProperty() {
			JavaBeanStringProperty p = (JavaBeanStringProperty) __fx_properties.get("name");
			if (p == null) {
				p = JavaBeanStringPropertyBuilder.create().bean(this).name("name").getter("getName").setter("setName").build();
				__fx_properties.put("name", p);
			}
			return p;
		}
		
		public void setName(String name) {
			super.setName(name);
			nameProperty().fireValueChangedEvent();
		}
	}

```

In a more complex Person bean - containing a list of Address beans and a reference to a Nationality bean for example, they would also be transformed into JavaFX2 beans. In that case, the transformed `Person` would contain a list of transformed `Address` beans and a reference to a transformed `Nationality` bean.

The JavaFX2 bean classes are created by manipulating Java byte code and instantiated by the fx-serials library, so the property methods (`nameProperty`in the above example) are only available through introspection. The good news is that the implemented `JFXProxy` interface defines a method which allows you to obtain any property:

```java

	public ReadOnlyJavaBeanProperty getProperty(String propertyName);
```

Quick Start
-----------

JavaFX2 beans can be obtained in two ways:

- Using `FxSerialsContext` to explicitly proxy one traditional Java Bean
- Serializing and deserializing an object stream containing traditional Java Beans (useful for RMI or HTTP Remoting)

Given a `Person` class which implements the `JFXSerializable` interface:


FxSerialsContext example:

```java

	FxSerialsContext transformer = new FxSerialsContext();
	
	Person person = new Person();
	person.setName("Carlos Martins");
	
	Person personFx = transformer.getProxy(person);
```

Serializing-Deserializing example

```java

	Person person = new Person();
	person.setName("Carlos Martins");

    // Serialize person to a file
	FileOutputStream fileOut = new FileOutputStream("person.ser");
	FxProxyCreatorOutputStream out = new FxProxyCreatorOutputStream(fileOut);
	out.writeObject(person);
	out.close();
	fileOut.close();

	// Read the serialized person into a JavaFX2 person
	FxProxyCreatorInputStream fxInputStream = new FxProxyCreatorInputStream(new FileInputStream("person.ser"););
    Person personFx = (Person) fxInputStream.readObject();
    fxInputStream.close();
```


Note: FXSerials uses [Javassist version 3](https://github.com/jboss-javassist/javassist "Javassist on Github")

This software is distributed under the Apache License Version 2.0.
