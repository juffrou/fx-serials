package org.juffrou.fx.seraials.dom;

import org.juffrou.fx.serials.FxSerials;

public class Address implements FxSerials {
	
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
