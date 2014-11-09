
package org.juffrou.fx.seraials;

import static org.junit.Assert.*;

import java.io.ObjectStreamClass;

import org.juffrou.fx.seraials.dom.ConcreteObject;
import org.juffrou.fx.seraials.dom.TestPersonFX;
import org.junit.Test;

public class PojoBeansTestCase {

	@Test
	public void test() {
		TestPersonFX testPersonFx = new TestPersonFX();
		
		testPersonFx.getProperty("name");
	}
	
	@Test
	public void testGetSerialUID() {
		long serialVersionUID = ObjectStreamClass.lookup(ConcreteObject.class).getSerialVersionUID();

		assertEquals(2978135627452915524L, serialVersionUID);
	}
}
