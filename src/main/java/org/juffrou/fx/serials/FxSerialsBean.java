package org.juffrou.fx.serials;

import javafx.beans.property.adapter.ReadOnlyJavaBeanProperty;

/**
 * A JavaFX Bean class with an extra utility method.
 * 
 * @author Carlos Martins
 */
public interface FxSerialsBean {

	@SuppressWarnings("rawtypes")
	public ReadOnlyJavaBeanProperty getProperty(String propertyName);
}
