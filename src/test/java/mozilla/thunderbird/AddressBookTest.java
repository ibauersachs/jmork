package mozilla.thunderbird;

import java.util.List;

import junit.framework.TestCase;

public class AddressBookTest extends TestCase {

	public void testNullInputStream() throws Exception {
		try {
			new AddressBook().load(null);
			fail("Exception expected");
		} catch (Exception expected) {
		}
	}
	
	public void testRows() throws Exception {
		AddressBook addressBook = new AddressBook();
		addressBook.load(getClass().getResourceAsStream("/simple.mab"));
	}
	
	public void testUrlInGroup() throws Exception {
		AddressBook addressBook = new AddressBook();
		addressBook.load(getClass().getResourceAsStream("/abook_urlingroup.mab"));
	}
	
	public void testAddressBookReader() throws Exception {
		AddressBook addressBook = new AddressBook();
		addressBook.load(getClass().getResourceAsStream("/abook_single.mab"));
		
		List<Address> addresses = addressBook.getAddresses();
		assertEquals(1,addresses.size());
		
//		Address address = addresses.get(0);
//		assertEquals("mike.haller@smartwerkz.com",address.getPrimaryEmail());
//		assertEquals("Mike",address.getFirstName());
//		assertEquals("Haller",address.getLastName());
//		assertEquals("Mike Haller",address.getDisplayName());
	}
	
	public void testAddressBookNoAtomDatabaseFound() throws Exception {
		AddressBook addressBook = new AddressBook();
		addressBook.load(getClass().getResourceAsStream("/abook_noatomdb.mab"));
	}

}
