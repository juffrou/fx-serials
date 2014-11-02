package org.juffrou.fx.seraials.dom;

import java.time.LocalDate;

import org.juffrou.fx.serials.FxSerials;

public class Person implements FxSerials {
	
	private static final long serialVersionUID = 6329998877045393661L;

	private Integer id;
	private String name;
	private String email;
	private LocalDate dateOfBirth;
	
	
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

}
