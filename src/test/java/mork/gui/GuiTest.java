package mork.gui;

import junit.framework.TestCase;

public class GuiTest extends TestCase {

	public void testFindThunderbirdProfile() throws Exception {
		ProfileLocator locator = new ProfileLocator();
		String result = locator.locateFirstThunderbirdAddressbookPath();
		assertNotNull(result);
		assertTrue(result.contains("Thunderbird"));
		assertTrue(result.contains("Profiles"));
	}

}
