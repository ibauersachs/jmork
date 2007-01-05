package mozilla.thunderbird;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class AddressTest extends TestCase {

	public void testAddress() throws Exception {
		Map<String,String> values = new HashMap<String, String>();
		values.put("Company","testCompany");
		values.put("DisplayName","testDisplayName");
		values.put("LastName","testLastName");
		values.put("FirstName","testFirstName");
		values.put("PrimaryEmail","testPrimaryEmail");
		Address address = new Address(values);
		assertEquals("testCompany",address.getCompany());
		assertEquals("testDisplayName",address.getDisplayName());
		assertEquals("testFirstName",address.getFirstName());
		assertEquals("testLastName",address.getLastName());
		assertEquals("testPrimaryEmail",address.getPrimaryEmail());
	}
	
}
