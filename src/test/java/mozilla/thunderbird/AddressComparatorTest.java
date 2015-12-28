package mozilla.thunderbird;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mork.Alias;

public class AddressComparatorTest extends AddressTest {

	public void testCompare() {
		Map<String, Alias> aliases1 = new HashMap<String, Alias>();
		put(aliases1, "LastName", "A");
		Address ad1 = new Address(aliases1);

		Map<String, Alias> aliases2 = new HashMap<String, Alias>();
		put(aliases2, "LastName", "Z");
		Address ad2 = new Address(aliases2);

		// Temp
		AddressComparator comparator = new AddressComparator();
		assertEquals(-25, comparator.compare(ad1, ad2));
		assertEquals(+25, comparator.compare(ad2, ad1));
		assertEquals(0, comparator.compare(ad1, ad1));
		assertEquals(0, comparator.compare(ad2, ad2));
		// Temp

		List<Address> list = new ArrayList<Address>();
		list.add(ad2);
		list.add(ad1);
		assertSame(ad2, list.get(0));
		assertSame(ad1, list.get(1));
		Collections.sort(list, comparator);
		assertSame(ad1, list.get(0));
		assertSame(ad2, list.get(1));
	}

	/**
	 * Fix for Illegal comparison method in Java 8
	 * 
	 * <pre>
	 * java.lang.IllegalArgumentException: Comparison method violates its general contract!
	 * 	at java.util.TimSort.mergeHi(TimSort.java:899)
	 * 	at java.util.TimSort.mergeAt(TimSort.java:516)
	 * 	at java.util.TimSort.mergeCollapse(TimSort.java:441)
	 * 	at java.util.TimSort.sort(TimSort.java:245)
	 * 	at java.util.Arrays.sort(Arrays.java:1512)
	 * 	at java.util.ArrayList.sort(ArrayList.java:1454)
	 * 	at java.util.Collections.sort(Collections.java:175)
	 * 	at mozilla.thunderbird.AddressComparatorTest.testCompareContract(AddressComparatorTest.java:46)
	 * </pre>
	 * 
	 * @throws Exception
	 */
	public void testCompareContract() throws Exception {

		AddressBook book = new AddressBook();
		book.load(getClass().getResourceAsStream("/abook_JMORK-3.mab"));
		List<Address> addresses = new ArrayList<Address>(book.getAddresses());
		Collections.sort(addresses, new AddressComparator());

	}

}
