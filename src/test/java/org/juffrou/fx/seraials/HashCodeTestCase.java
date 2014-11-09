package org.juffrou.fx.seraials;

import org.junit.Test;

public class HashCodeTestCase {

	@Test
	public void testHashCodes() {
		System.out.println("HASH_STRING = " + "String".hashCode());
		System.out.println("HASH_INTEGER = "+ "Integer".hashCode());
		System.out.println("HASH_LONG = " + "Long".hashCode());
		System.out.println("HASH_BOOLEAN = " + "Boolean".hashCode());
		System.out.println("HASH_DOUBLE = " + "Double".hashCode());
		System.out.println("HASH_FLOAT = " + "Float".hashCode());
	}
}
