Juffrou FXSerials
=================

_Automagical JavaFX2 Beans_

Copyright (C) 2014- by Carlos Martins, All rights reserved.

Transforms traditional Java Beans into JavaFX2 Beans by adding property methods which return the appropriate JavaFX property type.

Given a traditional java bean like the following:

```java
	
	package example.fxseraials

	public class Person implements FxSerials {
	
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

Its corresponding JavaFX2 Bean can be obtained by using one of two different methods:

- Deserializing an object stream containing traditional Java Beans
- Explicitly proxying one traditional Java Bean

Deserializing example:

```java

	Person person = new Person();
	person.setName("Carlos Martins");
	FileOutputStream fileOut = new FileOutputStream("person.ser");
	ObjectOutputStream out = new ObjectOutputStream(fileOut);
	out.writeObject(person);
	out.close();
	fileOut.close();

	fxInputStream = new FxInputStream(new FileInputStream("person.ser"););
    Person personFx = (Person) fxInputStream.readObject();
```

Transforming example:

```java

	FxSerialsUtil transformer = new FxSerialsUtil();
	
	Person person = new Person();
	person.setName("Carlos Martins");
	
	Person personFx = transformer.getProxy(person);
```

In both cases, the personFx object returned extends Person and implements the FxSerialsBean interface. This is what its code would look like:

```java

	package example.fxseraials._fx_

	public class Person extends example.fxseraials.Person implements FxSerialsBean {
	
		private static final long serialVersionUID = 6329998877045393661L;
		
		private Map<String, JavaBeanStringProperty> __fx_properties = new HashMap<String, JavaBeanStringProperty>();

		public ReadOnlyJavaBeanProperty getProperty(String propertyName) {
			Method m = getClass().getMethod(propertyName + "Property", null);
			return (ReadOnlyJavaBeanProperty) m.invoke(this, null);
		}
		
		public JavaBeanStringProperty nameProperty() {
			javafx.beans.property.adapter.JavaBeanStringProperty p = (javafx.beans.property.adapter.JavaBeanStringProperty) __fx_properties.get("name");
			if (p == null) {
				p = javafx.beans.property.adapter.JavaBeanStringPropertyBuilder.create().bean(this).name("name").getter("getName").setter("setName").build();
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

This class is not instantiated by you, so you can only access the method `nameProperty` through introspection. The good news in that the implemented `FxSerialsBean` interface defines a method which allows you to obtain any property:

```java

	public ReadOnlyJavaBeanProperty getProperty(String propertyName);
```

Note: FXSerials uses [Javassist version 3](https://github.com/jboss-javassist/javassist "Javassist on Github")

This software is distributed under the Apache License Version 2.0.
