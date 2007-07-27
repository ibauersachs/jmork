package mork;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Aliases class represents a list of key-value pairs. It is able to parse
 * groups of these pairs into a Map.
 * 
 * <p>
 * Such a group can occur in one of the following forms
 * </p>
 * <ul>
 * <li><code>(foo^80)</code></li>
 * <li><code>(foo=bar)</code></li>
 * <li><code>(^80^81)</code></li>
 * <li><code>(^80=bar)</code></li>
 * <li><code>(foo=(bar\))</code> - includes escaped closing bracket</li>
 * </ul>
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
		final StringReader reader = new StringReader(aliases);
		final StringBuffer alias = new StringBuffer();
		boolean inParentheses = false;
		try {
			int c = reader.read();
			while (c != -1) {
				switch (c) {
				case '\\':
					int escapedCharacter = reader.read();
					alias.append((char) escapedCharacter);
					break;
				case '(':
					inParentheses = true;
					alias.append((char) c);
					break;
				case ')':
					if (inParentheses) {
						alias.append((char) c);
						parseSingleAlias(dicts, alias.toString().trim());
						alias.setLength(0);
						inParentheses = false;
					}
					break;
				default:
					if (inParentheses) {
						alias.append((char) c);
					}
					break;
				}
				c = reader.read();
			}
		} catch (IOException e) {
			throw new RuntimeException("Format of alias not supported: "
					+ aliases);
		}
		// Pattern aliasesPattern = Pattern
		// .compile("(?:\\(\\^?[\\w\\s^=\\^]*[=\\^][^\\)]*\\))");
		// Matcher aliasesMatcher = aliasesPattern.matcher(aliases);
		// while (aliasesMatcher.find()) {
		// String alias = aliasesMatcher.group(); // (foo^80)
		// parseSingleAlias(dicts, alias);
		// }

	}

	/**
	 * Parses a single alias pair in the form <code>(key=value)</code> or
	 * similar. See class comment for possible variations.
	 * 
	 * @param dicts
	 *            list of dictionaries
	 * @param alias
	 *            the alias including parentheses, e.g. <code>(^80=bar)</code>
	 */
	private void parseSingleAlias(List<Dict> dicts, String alias) {
		if (alias.length() < 2) {
			throw new RuntimeException("Alias must be at least 2 characters");
		}
		if (alias.charAt(0) != '(' || alias.charAt(alias.length() - 1) != ')') {
			throw new RuntimeException(
					"Alias has wrong format, needs to be in parentheses: "
							+ alias);
		}
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
				value = Dict.dereference(value, dicts, ScopeTypes.ATOM_SCOPE);
			}
			dict.put(id.trim(), value);
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

	public void printAliases(PrintStream out) {
		out.println(dict);
	}

}
