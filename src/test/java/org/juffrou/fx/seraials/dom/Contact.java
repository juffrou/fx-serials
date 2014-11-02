package org.juffrou.fx.seraials.dom;

import org.juffrou.fx.serials.FxSerials;

public class Contact implements FxSerials {

	private static final long serialVersionUID = -2520658029258402172L;

	private String description;
	private String value;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
