package org.juffrou.fx.seraials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;

import org.juffrou.fx.seraials.dom.Address;
import org.juffrou.fx.seraials.dom.Contact;
import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.JFXProxy;
import org.juffrou.fx.serials.io.FxProxyCreatorInputStream;
import org.juffrou.fx.serials.io.FxProxyCreatorOutputStream;
import org.junit.Test;

import javafx.beans.property.ReadOnlyProperty;

public class FXInputStreamTestCase {
	
	private void writePerson() throws IOException {
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
		FileOutputStream fileOut = new FileOutputStream("person.ser");
		FxProxyCreatorOutputStream out = new FxProxyCreatorOutputStream(fileOut);
		out.writeObject(person);
		out.close();
		fileOut.close();
		System.out.println("Serialized data is saved in person.ser");
	}
	
	private void writeContact() throws IOException {
		Contact phone=new Contact();
		phone.setDescription("Mobile");
		phone.setValue("918 333 222");
		FileOutputStream fileOut = new FileOutputStream("contact.ser");
		FxProxyCreatorOutputStream out = new FxProxyCreatorOutputStream(fileOut);
		out.writeObject(phone);
		out.close();
		fileOut.close();
		System.out.println("Serialized data is saved in contact.ser");
	}
	
	@Test
	public void testReadSimpleObject() {
		
		FxProxyCreatorInputStream fxInputStream = null;
		FileInputStream fileIn = null;

		try {
			
			writeContact();
			fileIn = new FileInputStream("contact.ser");
			fxInputStream = new FxProxyCreatorInputStream(fileIn);
			
			// Deserialize contact
			Contact contact = null;
			contact = (Contact) fxInputStream.readObject();
			System.out.println("Received a " + contact.getClass().getName());

			assertTrue(JFXProxy.class.isAssignableFrom(contact.getClass()));
			
			JFXProxy fxContact = (JFXProxy) contact;
			assertEquals("Mobile", contact.getDescription());
			
			ReadOnlyProperty<?> property = fxContact.getProperty("description");
			System.out.println("Property description is " + property);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		finally {
			try {
				if(fxInputStream != null)
					fxInputStream.close();
				if(fileIn != null)
					fileIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
