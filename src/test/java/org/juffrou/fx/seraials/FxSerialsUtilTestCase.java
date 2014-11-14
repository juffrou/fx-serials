package org.juffrou.fx.seraials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;

import org.juffrou.fx.seraials.dom.Address;
import org.juffrou.fx.seraials.dom.ConcreteObject;
import org.juffrou.fx.seraials.dom.Contact;
import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.FxSerialsProxy;
import org.juffrou.fx.serials.FxSerialsUtil;
import org.juffrou.fx.serials.error.FxTransformerException;
import org.junit.Test;

public class FxSerialsUtilTestCase {

	@Test
	public void testFxTransformer() {
		FxSerialsUtil transformer = new FxSerialsUtil();
		
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
		
		transformer.getProxy(personFx);
		
	}
	
	@Test
	public void testAbstractClassExtension() {
		ConcreteObject o = new ConcreteObject();
		o.setName("My Object");
		
		FxSerialsUtil transformer = new FxSerialsUtil();

		ConcreteObject oFx = transformer.getProxy(o);
		
		assertTrue(FxSerialsProxy.class.isAssignableFrom(oFx.getClass()));
		
		FxSerialsProxy oFxBean = (FxSerialsProxy)oFx;
		
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
		FxSerialsUtil fxSerialsUtil = new FxSerialsUtil();

		ConcreteObject coProxy = fxSerialsUtil.getProxy(ConcreteObject.class);
		
		assertTrue(FxSerialsProxy.class.isAssignableFrom(coProxy.getClass()));
		
	}
	
	@Test
	public void testCollectionProxying() {
		FxSerialsUtil fxSerialsUtil = new FxSerialsUtil();
		
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

		proxy.forEach(c -> System.out.println(c));
		
		Contact contactProxy = proxy.get(0);
		assertEquals("mobile", contactProxy.getDescription());
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
