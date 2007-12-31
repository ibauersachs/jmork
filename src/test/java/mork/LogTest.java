package mork;

import junit.framework.TestCase;

public class LogTest extends TestCase {

	public void testLog() {
		Log log = new Log(this);
		assertTrue(log.getSourceClassname().contains(LogTest.class.getName()));
	}

	public void testWarn() {
		Log log = new Log(this);
		String result = log.warn("Foobar",new Exception());
		assertTrue(result.contains("WARN"));
		assertTrue(result.contains("[mork.LogTest]"));
		assertTrue(result.contains("java.lang.Exception"));
	}

}
