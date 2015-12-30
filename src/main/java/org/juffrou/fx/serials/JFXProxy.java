package org.juffrou.fx.serials;

import java.io.Serializable;

import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;


/**
 * Interface implemented by dynamically created proxies of traditional Java Beans which implement the FxSerials interface.<p>
 * An object implementing this interface extends a traditional Java Bean and implements the JavaFX2 Bean specification.<br>
 * Properties can be obtained by introspection using the "fieldnameProperty" pattern or by calling the getProperty method.
 * 
 * @author Carlos Martins
 */
public interface JFXProxy extends Serializable {

	/**
	 * Obtains the JavaFX2 property that corresponds to a specific property of this bean.<p>
	 * If there is no setter method for the specified property (is read only), then a read only
	 * JavaFX2 property will be obtained.
	 * @param propertyName name of the field for which to obtain the property
	 * @return the read only or read/write JavaFX2 property that corresponds to the field
	 */
	@SuppressWarnings("rawtypes")
	public ReadOnlyJavaBeanProperty getProperty(String propertyName);
}
