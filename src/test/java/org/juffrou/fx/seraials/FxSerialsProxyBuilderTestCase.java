package org.juffrou.fx.seraials;

import javafx.beans.property.Property;

import org.juffrou.fx.seraials.dom.Person;
import org.juffrou.fx.serials.FxSerialsUtil;
import org.juffrou.fx.serials.core.FxSerialsProxyBuilder;
import org.juffrou.fx.serials.error.FxSerialsProxyAlreadExistsException;
import org.junit.Test;

public class FxSerialsProxyBuilderTestCase {

	@Test
	public void test() {
		FxSerialsProxyBuilder proxyBuilder = new FxSerialsProxyBuilder();
			Class<?> buildFXSerialsProxyClass;
			try {
				buildFXSerialsProxyClass = proxyBuilder.buildFXSerialsProxy(Person.class, 0);
				Object proxy = buildFXSerialsProxyClass.newInstance();
				Property<String> property = (Property<String>) FxSerialsUtil.getProperty(proxy, "name");
				property.setValue("Carlos");
			} catch (FxSerialsProxyAlreadExistsException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	}
}
