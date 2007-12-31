package mork;

import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

public class BugStephanTest extends TestCase {

	public void testJMORK1() throws Exception {
		InputStreamReader reader = new InputStreamReader(getClass().getResource("/abook_stephan.mab").openStream());
		MorkDocument morkDocument = new MorkDocument(reader);

		List<Dict> dicts = morkDocument.getDicts();
		assertEquals(5,dicts.size());
		
		Dict dict0 = dicts.get(0);
		assertEquals(70,dict0.getAliasCount());
		assertSame(ScopeTypes.COLUMN_SCOPE,dict0.getDefaultScope());
		assertEquals("ns:addrbk:db:row:scope:card:all",dict0.getValue("80"));
		assertEquals("ns:addrbk:db:row:scope:list:all",dict0.getValue("81"));
		assertEquals("ns:addrbk:db:row:scope:data:all",dict0.getValue("82"));
		assertEquals("FirstName",dict0.getValue("83"));
		assertEquals("LastName",dict0.getValue("84"));
		assertEquals("PhoneticFirstName",dict0.getValue("85"));
		
		Dict dict2 = dicts.get(1);
		assertEquals(25,dict2.getAliasCount());
		assertSame(ScopeTypes.ATOM_SCOPE,dict2.getDefaultScope());
		assertEquals("Stephan Zeissler",dict2.getValue("81"));
		assertEquals("(KUTTIG)",dict2.getValue("82")); // Bug
		assertEquals("",dict2.getValue("83"));
		assertEquals("Stephan Zeissler (KUTTIG)",dict2.getValue("84")); // Bug
		assertEquals("1",dict2.getValue("85"));
	}
	
}
