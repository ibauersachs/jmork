package mozilla.thunderbird;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mork.ExceptionHandler;
import mork.MorkDocument;
import mork.Row;
import mork.Table;

/**
 * An address book is a container for addresses loaded from a Mozilla
 * Thunderbird address book, which is stored in the Mork file format.
 * 
 * @author mhaller
 */
public class AddressBook {

	/** Internal container for Addresses */
	private final List<Address> addresses = new LinkedList<Address>();
	private ExceptionHandler exceptionHandler;

	/**
	 * Loads a Mork database from the given input and parses it as being a
	 * Mozilla Thunderbird Address Book. The file is usually called abook.mab
	 * and is located in the Thunderbird user profile.
	 * 
	 * If additional address books are loaded into the same Address Book
	 * instance, the addresses get collected into the same address book.
	 * 
	 * @param inputStream
	 *            the stream to load the address book from.
	 */
	public void load(final InputStream inputStream) {
		if (inputStream == null) {
			throw new IllegalArgumentException("InputStream must not be null");
		}
		final MorkDocument morkDocument = new MorkDocument(
				new InputStreamReader(inputStream), exceptionHandler);
		for (Row row : morkDocument.getRows()) {
			final Address address = new Address(row.getValues());
			addresses.add(address);
		}
		for (Table table : morkDocument.getTables()) {
			for (Row row : table.getRows()) {
				if (row.getValue("DisplayName") != null) {
					final Address address = new Address(row.getValues());
					addresses.add(address);
				}
			}
		}
	}

	/**
	 * Returns an unmodifiable list of {@link Address}es.
	 * 
	 * @return an unmodifiable list of {@link Address}es, might be empty, never
	 *         null.
	 */
	public List<Address> getAddresses() {
		return Collections.unmodifiableList(addresses);
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
