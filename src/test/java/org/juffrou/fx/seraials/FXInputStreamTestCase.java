package org.juffrou.fx.seraials;

import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;

import org.juffrou.fx.seraials.dom.Address;
import org.juffrou.fx.seraials.dom.Contact;
import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.FxSerials;
import org.juffrou.fx.serials.FxSerialsProxy;
import org.juffrou.fx.serials.io.FxInputStream;
import org.junit.Test;

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
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
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
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(phone);
		out.close();
		fileOut.close();
		System.out.println("Serialized data is saved in contact.ser");
	}
	
	@Test
	public void testReadSimpleObject() {
		
		FxInputStream fxInputStream = null;
		FileInputStream fileIn = null;

		try {
			
			writeContact();
			fileIn = new FileInputStream("contact.ser");
			fxInputStream = new FxInputStream(fileIn);
			
			// Deserialize contact
			Contact contact = null;
			contact = (Contact) fxInputStream.readObject();
			System.out.println("Received a " + contact.getClass().getName());

			assertTrue(FxSerialsProxy.class.isAssignableFrom(contact.getClass()));
			
			FxSerialsProxy fxContact = (FxSerialsProxy) contact;
			assertEquals("Mobile", contact.getDescription());
			
			ReadOnlyJavaBeanProperty property = fxContact.getProperty("description");
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
	
	@Test
	public void testReadObject() {

		FxInputStream fxInputStream = null;
		FileInputStream fileIn = null;

		try {
			
			writePerson();
			fileIn = new FileInputStream("person.ser");
			fxInputStream = new FxInputStream(fileIn);
			
			// Deserialize person
			Person person = null;
			person = (Person) fxInputStream.readObject();
			System.out.println("Received a " + person.getClass().getName());
			assertTrue(FxSerialsProxy.class.isAssignableFrom(person.getClass()));
			FxSerialsProxy fxPerson = (FxSerialsProxy) person;
			ReadOnlyJavaBeanProperty property = fxPerson.getProperty("name");
			System.out.println("Property name is " + property);
			
			// Check that the Address was also proxied
			Address address = person.getAddress();
			assertNotNull(address);
			assertTrue(FxSerialsProxy.class.isAssignableFrom(address.getClass()));
			
			// test that a second call gets the same instance
			ReadOnlyJavaBeanProperty property2 = fxPerson.getProperty("name");
			assertEquals(property, property2);
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
	
	@Test
	public void testResolveProxy() {
		FxInputStream fxInputStream = null;
		FileInputStream fileIn = null;

		try {
			
			writePerson();
			fileIn = new FileInputStream("person.ser");
			fxInputStream = new FxInputStream(fileIn);
			
			// Auto-proxy person
			
			Class<?> personClass = fxInputStream.resolveProxyClass(new String[] {FxSerials.class.getName()});
			
			// Test the proxy returned
			System.out.println("Received a " + personClass.getName());

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
