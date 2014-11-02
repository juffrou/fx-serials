package org.juffrou.fx.seraials;

import java.time.LocalDate;

import org.juffrou.fx.seraials.dom.Address;
import org.juffrou.fx.seraials.dom.Contact;
import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.io.FxTransformer;
import org.junit.Test;

public class FxTransformerTestCase {

	@Test
	public void testFxTransformer() {
		FxTransformer transformer = new FxTransformer();
		
		Person person = new Person();
		person.setName("Reyan Ali");
		person.setEmail("Phokka Kuan, Ambehta Peer");
		person.setDateOfBirth(LocalDate.of(1967, 10, 1));
		Address address = new Address();
		address.setStreet("My Street");
		address.setDoor("Number 1");
		person.setAddress(address);
		Contact phone=new Contact();
		phone.setDescription("Mobile");
		phone.setValue("918 333 222");
		person.addContact(phone);

		Person personFx = transformer.transform(person);
		
		transformer.transform(personFx);
		
	}
}
