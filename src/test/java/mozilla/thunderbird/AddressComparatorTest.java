package mozilla.thunderbird;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class AddressComparatorTest extends TestCase {

	public void testCompare() {
		Map<String, String> values1 = new HashMap<String, String>();
		values1.put("LastName", "A");
		Address ad1 = new Address(values1);

		Map<String, String> values2 = new HashMap<String, String>();
		values2.put("LastName", "Z");
		Address ad2 = new Address(values2);

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

}
