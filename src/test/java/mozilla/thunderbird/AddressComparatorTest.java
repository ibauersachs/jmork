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
		put(aliases1,"LastName", "A");
		Address ad1 = new Address(aliases1);

		Map<String, Alias> aliases2 = new HashMap<String, Alias>();
		put (aliases2,"LastName", "Z");
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

}
