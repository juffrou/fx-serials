package org.juffrou.fx.seraials.dom;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.juffrou.fx.serials.JFXSerializable;

public class Person implements JFXSerializable {
	
	public static final long serialVersionUID = 6329998877045393661L;

	private Integer id;
	private String name;
	private String email;
	private LocalDate dateOfBirth;
	private Address address;
	private Set<Contact> contacts;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public Set<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(Set<Contact> contacts) {
		this.contacts = contacts;
	}
	public void addContact(Contact contact) {
		if(contacts == null)
			contacts = new HashSet<Contact>();
		contacts.add(contact);
	}
}
