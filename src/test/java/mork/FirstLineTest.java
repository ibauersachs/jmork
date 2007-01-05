package mork;

import junit.framework.TestCase;

public class FirstLineTest extends TestCase {

	public void testFirstLine1() {
		try {
			new FirstLine("");
			fail("RuntimeException expected");
		} catch (RuntimeException expected) {
			assertEquals(
					"Invalid Mork format: , should be: // <!-- <mdb:mork:z v=\"1.4\"/> -->",
					expected.getMessage());
		}
	}

	public void testGetVersion() {
		assertEquals("1.4",
				new FirstLine("// <!-- <mdb:mork:z v=\"1.4\"/> -->")
						.getVersion());
	}

}
