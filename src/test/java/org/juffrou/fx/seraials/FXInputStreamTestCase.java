package org.juffrou.fx.seraials;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;

import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;

import org.juffrou.fx.seraials.dom.Address;
import org.juffrou.fx.seraials.dom.Contact;
import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.FxSerialsProxy;
import org.juffrou.fx.serials.io.FxInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FXInputStreamTestCase {
	
	FxInputStream fxInputStream = null;
	FileInputStream fileIn = null;
	
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
	
	@Before
	public void setup() {
		try {
			writePerson();
			fileIn = new FileInputStream("person.ser");
			fxInputStream = new FxInputStream(fileIn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@After
	public void tearDown() {
		try {
			if(fxInputStream != null)
				fxInputStream.close();
			if(fileIn != null)
				fileIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
		try {
			Person person = (Person) fxInputStream.readObject();
			System.out.println("Received a " + person.getClass().getName());
			assertTrue(FxSerialsProxy.class.isAssignableFrom(person.getClass()));
			FxSerialsProxy fxPerson = (FxSerialsProxy) person;
			ReadOnlyJavaBeanProperty property = fxPerson.getProperty("name");
			System.out.println("Property name is " + property);
			// test that a second call gets the same instance
			ReadOnlyJavaBeanProperty property2 = fxPerson.getProperty("name");
			assertEquals(property, property2);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
