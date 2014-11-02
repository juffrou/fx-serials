package org.juffrou.fx.seraials;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.Date;

import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.io.FxInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FXInputStreamTestCase {
	
	FxInputStream fxInputStream = null;
	FileInputStream fileIn = null;
	
	private void writePerson() throws IOException {
		Person e = new Person();
		e.setName("Reyan Ali");
		e.setEmail("Phokka Kuan, Ambehta Peer");
		e.setDateOfBirth(LocalDate.of(1967, 10, 1));
		FileOutputStream fileOut = new FileOutputStream("person.ser");
		ObjectOutputStream out = new ObjectOutputStream(fileOut);
		out.writeObject(e);
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
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
