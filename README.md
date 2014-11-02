fx-serials
==========

Transforms traditional Java Beans into JavaFX2 Beans by adding property methods which return the appropriate JavaFX property type.

JavaFX2 Beans cann be obtained by two ways:

- Deserializing an object stream containing traditional Java Beans
- Explicitly transforming one traditional Java Bean

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

	FxTransformer transformer = new FxTransformer();
	
	Person person = new Person();
	person.setName("Carlos Martins");
	
	Person personFx = transformer.transform(person);
```

In both cases, the personFx object returned extends Person, implements the FxSerialsBean interface and contains the following property method for the name property: 

```java

	public JavaBeanStringProperty nameProperty() {...}
```

Property methods may be accessed through introspection, but the FxSerialsBean interface defines a method which allows you to obtain any property:

```java

	public ReadOnlyJavaBeanProperty getProperty(String propertyName);
```
