package mork;

import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private Map<String, Alias> dict = new HashMap<String, Alias>();

	/**
	 * Parse the definition of Aliases without using a dictionary.
	 * 
	 * @param aliases
	 *          the aliases definition
	 */
	public Aliases(String aliases) {
		this(aliases, Dict.EMPTY_LIST);
	}

	/**
	 * Parse the definition of Aliases using the given dictionaries to resolve
	 * references to keys and values.
	 * 
	 * @param aliases
	 *          the aliases definition to parse
	 * @param dicts
	 *          a list of dictionaries used to resolve keys and literal values
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
			throw new RuntimeException("Format of alias not supported: " + aliases);
		}
	}

	/**
	 * Empty aliases, e.g. for rows which contain a transaction group but no
	 * values
	 */
	public Aliases() {
	}

	/**
	 * Parses a single alias pair in the form <code>(key=value)</code> or similar.
	 * See class comment for possible variations.
	 * 
	 * @param dicts
	 *          list of dictionaries
	 * @param aliasStr
	 *          the alias including parentheses, e.g. <code>(^80=bar)</code>
	 */
	private void parseSingleAlias(final List<Dict> dicts, final String aliasStr) {
		if (aliasStr.length() < 3) {
			throw new RuntimeException("Alias must be at least 3 characters: "
					+ aliasStr);
		}
		final String withoutParentheses = aliasStr.substring(1, aliasStr.length() - 1);
		boolean isLiteral = withoutParentheses.indexOf('=') != -1;
		if (isLiteral) {
			String refid = aliasStr.substring(1, aliasStr.indexOf('='));
			String id=refid;
			if (id.startsWith("^")) {
				id = Dict.dereference(id, dicts, ScopeTypes.COLUMN_SCOPE);
			}
			String value = aliasStr
					.substring(aliasStr.indexOf('=') + 1, aliasStr.length() - 1);
			Alias alias=new Alias(refid,id,value,null);
			dict.put(id.trim(), alias);
		} else {
			String refid = aliasStr.substring(1, aliasStr.indexOf('^', 2));
			String id=refid;
			String valueref = aliasStr.substring(aliasStr.indexOf('^', 2), aliasStr.length() - 1);
			if (id.startsWith("^")) {
				id = Dict.dereference(id, dicts, ScopeTypes.COLUMN_SCOPE);
			}
			String value=valueref;
			if (valueref.startsWith("^")) {
				value = Dict.dereference(value, dicts, ScopeTypes.ATOM_SCOPE);
			}
			Alias alias=new Alias(refid,id,value,valueref);
			dict.put(id.trim(), alias);
		}
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
	 * value is the literal, dereferenced value. The id is also dereferenced using
	 * the dictionaries, if available.
	 * 
	 * @return an unmodifiable map of key/value pairs (the aliases) which have
	 *         been parsed.
	 */
	public Map<String, Alias> getAliases() {
		return Collections.unmodifiableMap(dict);
	}

	public void printAliases(PrintStream out) {
		out.println(dict);
	}

	/**
	 * get the set of keys
	 * 
	 * @return
	 */
	public Set<String> getKeySet() {
		return dict.keySet();
	}

	/**
	 * Returns a specific value of the given key
	 * 
	 * @param id
	 *          the key to get a literal value for. Keys are being trimmed before
	 *          the lookup.
	 * @return the dereferenced literal value of the alias with the given id,
	 *         might return <code>null</code> if no value for the given key is
	 *         available.
	 */
	public String getValue(String id) {
		Alias alias = dict.get(id.trim());
		if (alias != null)
			return alias.getValue();
		else
			return null;
	}

	/**
	 * returns a specific alias of a given key
	 * 
	 * @param id
	 * @return
	 */
	public Alias getAlias(String id) {
		return dict.get(id.trim());
	}
}
