package org.juffrou.fx.seraials.dom;

import org.juffrou.fx.serials.JFXSerializable;

public class Address implements JFXSerializable {
	
	// do not add serialVersionUUID

	private String street;
	private String door;
	
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getDoor() {
		return door;
	}
	public void setDoor(String door) {
		this.door = door;
	}
	
	
}
