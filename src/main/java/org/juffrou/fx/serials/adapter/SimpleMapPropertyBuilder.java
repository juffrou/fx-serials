package org.juffrou.fx.serials.adapter;

import java.util.Map;

import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class SimpleMapPropertyBuilder {

	private CollectionPropertyBuilderHelper builderHelper;
	
	private SimpleMapPropertyBuilder() {
		builderHelper = new CollectionPropertyBuilderHelper();
	}

	public static SimpleMapPropertyBuilder create() {
		return new SimpleMapPropertyBuilder();
	}
	
	public SimpleMapProperty<?,?> build() {
		
		Map<?,?> collection = (Map<?,?>) builderHelper.getCollection();
		ObservableMap<?,?> om = collection != null ? FXCollections.observableMap(collection) : FXCollections.observableHashMap();
		SimpleMapProperty<?,?> smp = new SimpleMapProperty(builderHelper.getBean(), builderHelper.getName(), om);
		
		return smp;
	}
	
	public SimpleMapPropertyBuilder bean(Object bean) {
		builderHelper.setBean(bean);;
		return this;
	}

	public SimpleMapPropertyBuilder name(String name) {
		builderHelper.setName(name);
		return this;
	}

	public SimpleMapPropertyBuilder getter(String getterName) {
		builderHelper.setGetterName(getterName);
		return this;
	}

	public SimpleMapPropertyBuilder setter(String setterName) {
		builderHelper.setSetterName(setterName);
		return this;
	}
	
}
