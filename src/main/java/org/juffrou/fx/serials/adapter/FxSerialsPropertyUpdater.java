package org.juffrou.fx.serials.adapter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;

public class FxSerialsPropertyUpdater {

	/**
	 * Called by JFXProxy instances every time a property of type List in the original Java Bean is changed.
	 * @param property Property of type list
	 * @param newBackingList New list for the property or null
	 * @param <E> Type of the list elements
	 */
	public static <E> void updateSimpleListProperty(SimpleListProperty<E> property, List<E> newBackingList) {
		ObservableList<E> ol = newBackingList != null ? FXCollections.observableList(newBackingList) : FXCollections.observableArrayList();
		property.set(ol);
	}
	
	/**
	 * Called by JFXProxy instances every time a property of type Set in the original Java Bean is changed.
	 * @param property Property of type Set
	 * @param newBackingSet New set for the property or null
	 * @param <E> Type of the set elements
	 */
	public static <E> void updateSimpleSetProperty(SimpleSetProperty<E> property, Set<E> newBackingSet) {
		ObservableSet<E> os = newBackingSet != null ? FXCollections.observableSet(newBackingSet) : FXCollections.observableSet();
		property.set(os);
	}


	/**
	 * Called by JFXProxy instances every time a property of type Map in the original Java Bean is changed.
	 * @param property Property of type Map
	 * @param newBackingMap New map for the property or null
	 * @param <K> Type of map keys
	 * @param <V> Type of map values
	 */
	public static <K,V> void updateSimpleMapProperty(SimpleMapProperty<K,V> property, Map<K,V> newBackingMap) {
		ObservableMap<K,V> om = newBackingMap != null ? FXCollections.observableMap(newBackingMap) : FXCollections.observableHashMap();
		property.set(om);
	}

}
