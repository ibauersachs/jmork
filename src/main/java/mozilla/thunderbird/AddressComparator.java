package mozilla.thunderbird;

import java.util.Comparator;

public class AddressComparator implements Comparator<Address> {

	public int compare(Address o1, Address o2) {
		if (o1.getLastName() != null && o2.getLastName() != null) {
			return o1.getLastName().compareToIgnoreCase(o2.getLastName());
		}
		if (o1.getDisplayName() != null && o2.getDisplayName() != null) {
			return o1.getDisplayName().compareToIgnoreCase(o2.getDisplayName());
		}
		if (o1.getPrimaryEmail() != null && o2.getPrimaryEmail() != null) {
			return o1.getPrimaryEmail().compareToIgnoreCase(
					o2.getPrimaryEmail());
		}
		return 0;
	}

}
