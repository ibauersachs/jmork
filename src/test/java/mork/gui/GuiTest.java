package mork.gui;

import junit.framework.TestCase;

public class GuiTest extends TestCase {

	public void testFindThunderbirdProfile() throws Exception {
		ProfileLocator locator = new ProfileLocator();
		String result = locator.locateFirstThunderbirdAddressbookPath();
		assertNotNull(result);
		if (result.contains("Thunderbird") && result.contains("Profiles")) {
			return;
		}
		if (result.contains(".mozilla-thunderbird")) {
			return;
		}
		fail("Unknown Thunderbird location: " + result);
	}

}
