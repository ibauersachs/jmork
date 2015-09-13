package mozilla.thunderbird;

import java.util.HashMap;

import mork.Alias;

public class AddressAccessTest extends AddressTest {
	
	public void testAddress() throws Exception {
		values = new HashMap<String, Alias>();
		put("Company","testCompany");
		put("DisplayName","testDisplayName");
		put("LastName","testLastName");
		put("FirstName","testFirstName");
		put("PrimaryEmail","testPrimaryEmail");
		Address address = new Address(values);
		assertEquals("testCompany",address.getCompany());
		assertEquals("testDisplayName",address.getDisplayName());
		assertEquals("testFirstName",address.getFirstName());
		assertEquals("testLastName",address.getLastName());
		assertEquals("testPrimaryEmail",address.getPrimaryEmail());
	}
	
}
