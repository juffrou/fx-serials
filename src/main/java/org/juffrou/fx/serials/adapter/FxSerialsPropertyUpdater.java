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
	 * @param property
	 * @param newBackingList
	 */
	public static <E> void updateSimpleListProperty(SimpleListProperty<E> property, List<E> newBackingList) {
		ObservableList<E> ol = newBackingList != null ? FXCollections.observableList(newBackingList) : FXCollections.observableArrayList();
		property.set(ol);
	}
	
	/**
	 * Called by JFXProxy instances every time a property of type List in the original Java Bean is changed.
	 * @param property
	 * @param newBackingSet
	 */
	public static <E> void updateSimpleSetProperty(SimpleSetProperty<E> property, Set<E> newBackingSet) {
		ObservableSet<E> os = newBackingSet != null ? FXCollections.observableSet(newBackingSet) : FXCollections.observableSet();
		property.set(os);
	}


	public static <K,V> void updateSimpleMapProperty(SimpleMapProperty<K,V> property, Map<K,V> newBackingMap) {
		ObservableMap<K,V> om = newBackingMap != null ? FXCollections.observableMap(newBackingMap) : FXCollections.observableHashMap();
		property.set(om);
	}

}
