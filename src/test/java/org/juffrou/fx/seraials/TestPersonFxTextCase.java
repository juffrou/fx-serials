package org.juffrou.fx.seraials;

import org.juffrou.fx.seraials.dom.TestPersonFX;
import org.junit.Test;

public class TestPersonFxTextCase {

	@Test
	public void test() {
		TestPersonFX testPersonFx = new TestPersonFX();
		
		testPersonFx.getProperty("name");
	}
}
