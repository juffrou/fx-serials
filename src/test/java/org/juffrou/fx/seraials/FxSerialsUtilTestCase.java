package org.juffrou.fx.seraials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.juffrou.fx.seraials.dom.Address;
import org.juffrou.fx.seraials.dom.ConcreteObject;
import org.juffrou.fx.seraials.dom.Contact;
import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.FxSerialsContext;
import org.juffrou.fx.serials.JFXProxy;
import org.juffrou.fx.serials.error.FxTransformerException;
import org.junit.Test;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.adapter.JavaBeanProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;

public class FxSerialsUtilTestCase {
	
	private Person createPerson() {

		Person person = new Person();
		person.setName("John Doe");
		person.setEmail("john@doe.net");
		person.setDateOfBirth(LocalDate.of(1967, 10, 1));
		Address address = new Address();
		address.setStreet("Dark Street");
		address.setDoor("Number 1");
		person.setAddress(address);
		Contact phone=new Contact();
		phone.setDescription("Mobile");
		phone.setValue("918 333 222");
		person.addContact(phone);
		person.addNicknames("Nick");
		
		Person spouse = new Person();
		spouse.setName("Jane Doe");
		spouse.setEmail("jane@doe.net");
		spouse.setAddress(address);
		spouse.setDateOfBirth(LocalDate.of(1966, 6, 21));
		
		person.addRelation("spouse", spouse);
		
		return person;
	}

	@Test
	public void testFxTransformer() {
		FxSerialsContext transformer = new FxSerialsContext();
		
		Person person = createPerson();

		Person personFx = transformer.getProxy(person);
		
		assertNotNull(personFx);
		assertTrue(JFXProxy.class.isAssignableFrom(personFx.getClass()));
		
		Person secondProxy = transformer.getProxy(personFx);
		assertTrue(secondProxy == personFx);
		
		Address addressFx = personFx.getAddress();
		assertNotNull(addressFx);
		assertTrue(JFXProxy.class.isAssignableFrom(addressFx.getClass()));
		
		ReadOnlyProperty<?> property = ((JFXProxy)personFx).getProperty("contacts");
		assertTrue("Espected a SimpleListProperty", SimpleListProperty.class == property.getClass());

		property = ((JFXProxy)personFx).getProperty("nicknames");
		assertTrue("Espected a SimpleSetProperty", SimpleSetProperty.class == property.getClass());

		property = ((JFXProxy)personFx).getProperty("relations");
		assertTrue("Espected a SimpleMapProperty", SimpleMapProperty.class == property.getClass());

		List<Contact> contacts = personFx.getContacts();
		for(Contact contactFx : contacts)
			assertTrue(JFXProxy.class.isAssignableFrom(contactFx.getClass()));
		
	}
	
	@Test
	public void testAbstractClassExtension() {
		ConcreteObject o = new ConcreteObject();
		o.setName("My Object");
		
		FxSerialsContext transformer = new FxSerialsContext();

		ConcreteObject oFx = transformer.getProxy(o);
		
		assertTrue(JFXProxy.class.isAssignableFrom(oFx.getClass()));
		
		JFXProxy oFxBean = (JFXProxy)oFx;
		
		JavaBeanStringProperty property = (JavaBeanStringProperty) oFxBean.getProperty("name");
		
		System.out.println(property);
		
		ConcreteObject proxy = (ConcreteObject) oFxBean;
		
		Property<String> other = new SimpleStringProperty();
		
		other.bindBidirectional(property);
		
		proxy.setName("Carlos");
		
		String value = other.getValue();
		
		assertEquals("Carlos", value);
	}

	@Test
	public void testClassProxying() {
		FxSerialsContext fxSerialsUtil = new FxSerialsContext();

		ConcreteObject coProxy = fxSerialsUtil.getProxy(ConcreteObject.class);
		
		assertTrue(JFXProxy.class.isAssignableFrom(coProxy.getClass()));
		
	}
	
	@Test
	public void testCollectionProxying() {
		FxSerialsContext fxSerialsUtil = new FxSerialsContext();
		
		List<Contact> contacts = new ArrayList<>();
		
		Contact contact = new Contact();
		contact.setDescription("mobile");
		contact.setValue("91xxxxxx");
		contacts.add(contact);

		contact = new Contact();
		contact.setDescription("land");
		contact.setValue("21xxxxxx");
		contacts.add(contact);
		
		List<Contact> serializeAndDeserialize = serializeAndDeserialize(contacts);
		serializeAndDeserialize.forEach(c -> System.out.println(c));
		
		List<Contact> proxy = fxSerialsUtil.getProxy(contacts);
		
		assertNotNull(proxy);
		
		proxy.forEach(c -> System.out.println(c));
		
		Contact contactProxy = proxy.get(0);
		assertEquals("mobile", contactProxy.getDescription());
		assertEquals("91xxxxxx", contactProxy.getValue());
		
		assertTrue(JFXProxy.class.isAssignableFrom(contactProxy.getClass()));
		
		ReadOnlyProperty property = ((JFXProxy)contactProxy).getProperty("description");
		
		assertNotNull(property);
		assertEquals("mobile", property.getValue());
	}
	
	@Test
	public void testProxyToFXBeanAndBackToOriginal() {
		FxSerialsContext transformer = new FxSerialsContext();
		
		Person person = new Person();
		person.setName("Carlos Martins");
		person.setEmail("carlos@martins.net");
		person.setDateOfBirth(LocalDate.of(1967, 10, 1));
		Address address = new Address();
		address.setStreet("My Street");
		address.setDoor("Number 1");
		person.setAddress(address);
		Contact phone=new Contact();
		phone.setDescription("Mobile");
		phone.setValue("918 333 222");
		person.addContact(phone);

		Person personFx = transformer.getProxy(person);
		
		assertNotNull(personFx);
		assertTrue(JFXProxy.class.isAssignableFrom(personFx.getClass()));
		
		JavaBeanProperty<String> property = (JavaBeanProperty<String>) transformer.getProperty(personFx, "name");
		property.setValue("Gugas");

		Person originalPerson = (Person) transformer.getOriginalBean(personFx);
		
		assertEquals("Gugas", originalPerson.getName());
		
		Person secondProxy = transformer.getProxy(personFx);
		assertTrue(secondProxy == personFx);
		
		Address addressFx = personFx.getAddress();
		assertNotNull(addressFx);
		assertTrue(JFXProxy.class.isAssignableFrom(addressFx.getClass()));
		
		List<Contact> contacts = personFx.getContacts();
		for(Contact contactFx : contacts)
			assertTrue(JFXProxy.class.isAssignableFrom(contactFx.getClass()));

	}
	
	private <T> T serializeAndDeserialize(T bean) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(bos);
			out.writeObject(bean);
			out.flush();
			out.close();
			
			ObjectInputStream oiStream = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
			return (T) oiStream.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new FxTransformerException("Error deserializing bean", e);
		}
	}

}
