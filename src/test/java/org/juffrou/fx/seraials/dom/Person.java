package org.juffrou.fx.seraials.dom;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.juffrou.fx.serials.JFXSerializable;

public class Person implements JFXSerializable {
	
	public static final long serialVersionUID = 6329998877045393661L;

	private Integer id;
	private String name;
	private String email;
	private LocalDate dateOfBirth;
	private Address address;
	private List<Contact> contacts;
	private Set<String> nicknames;
	private Map<String, Person> relations;
	
	
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
	public List<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	public void addContact(Contact contact) {
		if(contacts == null)
			setContacts(new ArrayList<Contact>());
		contact.setPerson(this);
		contacts.add(contact);
	}
	public Set<String> getNicknames() {
		return nicknames;
	}
	public void setNicknames(Set<String> nicknames) {
		this.nicknames = nicknames;
	}
	public void addNicknames(String nickname) {
		if(nicknames == null)
			setNicknames(new HashSet<String>());
		nicknames.add(nickname);
		
	}
	public Map<String, Person> getRelations() {
		return relations;
	}
	public void setRelations(Map<String, Person> relations) {
		this.relations = relations;
	}
	public void addRelation(String relation, Person relative) {
		if(relations == null)
			setRelations(new HashMap());
		relations.put(relation, relative);
	}
}
