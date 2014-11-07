package org.juffrou.fx.seraials;

import static org.junit.Assert.*;

import java.time.LocalDate;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.adapter.JavaBeanStringProperty;

import org.juffrou.fx.seraials.dom.Address;
import org.juffrou.fx.seraials.dom.ConcreteObject;
import org.juffrou.fx.seraials.dom.Contact;
import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.FxSerialsProxy;
import org.juffrou.fx.serials.FxSerialsUtil;
import org.junit.Test;

public class FxTransformerTestCase {

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

}
