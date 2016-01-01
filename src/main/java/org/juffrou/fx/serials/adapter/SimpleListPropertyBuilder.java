package org.juffrou.fx.serials.adapter;

import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SimpleListPropertyBuilder {

	private CollectionPropertyBuilderHelper builderHelper;
	
	private SimpleListPropertyBuilder() {
		builderHelper = new CollectionPropertyBuilderHelper();
	}

	public static SimpleListPropertyBuilder create() {
		return new SimpleListPropertyBuilder();
	}
	
	public SimpleListProperty<?> build() {
		
		List<?> collection = (List<?>) builderHelper.getCollection();
		ObservableList<?> ol = collection != null ? FXCollections.observableList(collection) : FXCollections.observableArrayList();
		SimpleListProperty<?> slp = new SimpleListProperty(ol);
		
		return slp;
	}
	
	public SimpleListPropertyBuilder bean(Object bean) {
		builderHelper.setBean(bean);;
		return this;
	}

	public SimpleListPropertyBuilder name(String name) {
		builderHelper.setName(name);
		return this;
	}

	public SimpleListPropertyBuilder getter(String getterName) {
		builderHelper.setGetterName(getterName);
		return this;
	}

	public SimpleListPropertyBuilder setter(String setterName) {
		builderHelper.setSetterName(setterName);
		return this;
	}
	
}
