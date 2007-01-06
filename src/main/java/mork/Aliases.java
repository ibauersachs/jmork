package mork;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Aliases class represents a list of key-value pairs. It is able to parse
 * groups of these pairs into a Map.
 * 
 * @author mhaller
 */
public class Aliases {

	/** Internal storage for keys and values */
	private Map<String, String> dict = new HashMap<String, String>();

	/**
	 * Parse the definition of Aliases without using a dictionary.
	 * 
	 * @param aliases
	 *            the aliases definition
	 */
	public Aliases(String aliases) {
		this(aliases, Dict.EMPTY_LIST);
	}

	/**
	 * Parse the definition of Aliases using the given dictionaries to resolve
	 * references to keys and values.
	 * 
	 * @param aliases
	 *            the aliases definition to parse
	 * @param dicts
	 *            a list of dictionaries used to resolve keys and literal values
	 */
	public Aliases(String aliases, List<Dict> dicts) {
		Pattern aliasesPattern = Pattern
				.compile("(?:\\(\\^?[\\w\\s^=\\^]*[=\\^][^\\)]*\\))");
		Matcher aliasesMatcher = aliasesPattern.matcher(aliases);
		while (aliasesMatcher.find()) {
			String alias = aliasesMatcher.group(); // (foo^80)
			String withoutParentheses = alias.substring(1, alias.length() - 1);
			boolean isLiteral = withoutParentheses.indexOf('=') != -1;
			if (isLiteral) {
				String id = alias.substring(1, alias.indexOf('='));
				if (id.startsWith("^")) {
					id = Dict.dereference(id, dicts, ScopeTypes.COLUMN_SCOPE);
				}
				String value = alias.substring(alias.indexOf('=') + 1, alias
						.length() - 1);
				dict.put(id.trim(), value);
			} else {
				String id = alias.substring(1, alias.indexOf('^', 2));
				String value = alias.substring(alias.indexOf('^', 2), alias
						.length() - 1);
				if (id.startsWith("^")) {
					id = Dict.dereference(id, dicts, ScopeTypes.COLUMN_SCOPE);
				}
				if (value.startsWith("^")) {
					value = Dict.dereference(value, dicts,
							ScopeTypes.ATOM_SCOPE);
				}
				dict.put(id.trim(), value);
			}
		}

	}

	/**
	 * Returns a specific value of the given key
	 * 
	 * @param id
	 *            the key to get a literal value for. Keys are being trimmed
	 *            before the lookup.
	 * @return the dereferenced literal value of the alias with the given id,
	 *         might return <code>null</code> if no value for the given key is
	 *         available.
	 */
	public String getValue(String id) {
		return dict.get(id.trim());
	}

	/**
	 * Returns the number of aliases which have been parsed
	 * 
	 * @return the count of aliases
	 */
	public int count() {
		return dict.size();
	}

	/**
	 * Returns a Map of Key/Value pairs. The key is the id of the alias and the
	 * value is the literal, dereferenced value. The id is also dereferenced
	 * using the dictionaries, if available.
	 * 
	 * @return an unmodifiable map of key/value pairs (the aliases) which have
	 *         been parsed.
	 */
	public Map<String, String> getValues() {
		return Collections.unmodifiableMap(dict);
	}

}
