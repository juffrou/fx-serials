package org.juffrou.fx.serials.adapter;

import java.util.Set;

import javafx.beans.property.SimpleSetProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class SimpleSetPropertyBuilder {

	private CollectionPropertyBuilderHelper builderHelper;
	
	private SimpleSetPropertyBuilder() {
		builderHelper = new CollectionPropertyBuilderHelper();
	}

	public static SimpleSetPropertyBuilder create() {
		return new SimpleSetPropertyBuilder();
	}
	
	public SimpleSetProperty<?> build() {
		
		Set<?> collection = (Set<?>) builderHelper.getCollection();
		ObservableSet<?> os = collection != null ? FXCollections.observableSet(collection) : FXCollections.observableSet();
		SimpleSetProperty<?> slp = new SimpleSetProperty(os);
		
		return slp;
	}
	
	public SimpleSetPropertyBuilder bean(Object bean) {
		builderHelper.setBean(bean);;
		return this;
	}

	public SimpleSetPropertyBuilder name(String name) {
		builderHelper.setName(name);
		return this;
	}

	public SimpleSetPropertyBuilder getter(String getterName) {
		builderHelper.setGetterName(getterName);
		return this;
	}

	public SimpleSetPropertyBuilder setter(String setterName) {
		builderHelper.setSetterName(setterName);
		return this;
	}
	
}
