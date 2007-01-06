package mork;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A dictionary contains key/value pairs. The keys are used in data cells (e.g.
 * Aliases Definitions) to compress data and reference the values stored in
 * dictionaries.
 * 
 * Each dictionary can optionally be categorized into a scope. The default scope
 * is the Atom Scope, which is used for literal values of actual content (in
 * contrast to the Column Scope, which us used for header data only)
 * 
 * @author mhaller
 */
public class Dict {

	/** A typed empty list of dictionaries */
	public static final List<Dict> EMPTY_LIST = new ArrayList<Dict>(0);

	/** The name of the scope, e.g. 'a' or 'atomScope' */
	private String scopeName;

	/** The value of the scope, usually 'c' */
	private String scopeValue;

	/**
	 * Internal reference to aliases which were included in the Dictionary
	 * definition
	 */
	private Aliases aliases;

	/**
	 * Parse a Dictionary using the given String content. The simplest
	 * dictionary possible is <code>&gt;&lt;</code>.
	 * 
	 * @param dictString
	 *            a valid dictionary definition
	 */
	public Dict(String dictString) {
		dictString = StringUtils.removeCommentLines(dictString);
		dictString = StringUtils.removeNewlines(dictString);

		Pattern pattern = Pattern.compile(
				"\\s*<\\s*(<\\(?.*\\)?>)?[\\s\\n\\r]*(.*)>[\\s\\r\\n]*",
				Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(dictString);
		if (!matcher.find()) {
			throw new RuntimeException("RegEx does not match: " + dictString);
		}
		String scopeDef = matcher.group(1);
		String aliasesDef = matcher.group(2);

		// Scope
		if (scopeDef != null) {
			Pattern scopePattern = Pattern.compile("<\\(?(.*)=([^\\)])\\)?>");
			Matcher scopeMatcher = scopePattern.matcher(scopeDef);
			if (scopeMatcher.matches()) {
				scopeName = scopeMatcher.group(1);
				scopeValue = scopeMatcher.group(2);
			}
		}

		// Aliases
		aliases = new Aliases(aliasesDef);
	}

	/**
	 * Returns the default scope of the parsed dictionary. This is not
	 * necessarily the same as the "global default scope", which is the Atom
	 * Scope.
	 * 
	 * Since Aliases itself could be scoped, a dictionary has a its own default
	 * scope for contained non-scoped aliases.
	 * 
	 * @return the default scope of the Dictionary, one of {@link ScopeTypes}
	 */
	public ScopeTypes getDefaultScope() {
		if (scopeValue != null
				&& scopeValue.toLowerCase(Locale.getDefault()).startsWith("c")) {
			return ScopeTypes.COLUMN_SCOPE;
		}
		return ScopeTypes.ATOM_SCOPE;
	}

	/**
	 * Returns the name of the scope,if the Dictionary included a scope
	 * definition.
	 * 
	 * @return the name of the scope of the Dictionary, if there was any, or
	 *         <code>null</code>
	 */
	public String getScopeName() {
		return scopeName;
	}

	/**
	 * Returns the value of the scope, if the Dictionary has any.
	 * 
	 * @return the value of the scope, or <code>null</code> if there was no
	 *         explicit scope definition.
	 */
	public String getScopeValue() {
		return scopeValue;
	}

	/**
	 * Returns the value of a parsed alias, if the Dictionary declared it.
	 * 
	 * @param id
	 *            the Alias Key to dereference
	 * @return the value of the alias with the key id
	 */
	public String getValue(String id) {
		return aliases.getValue(id);
	}

	/**
	 * Returns the number of aliases available in this Dictionary
	 * 
	 * @return the count number of aliases available
	 */
	public int getAliasCount() {
		return aliases.count();
	}

	/**
	 * Dereferences a pointer to a value using this dictionary.
	 * 
	 * @param id
	 *            the id with the "^"-Prefix
	 */
	public String dereference(String id) {
		if (!id.startsWith("^")) {
			throw new RuntimeException(
					"dereference() must be called with a reference id including the prefix '^'");
		}
		String oid = id.substring(1);
		String value = getValue(oid);
		return value;
	}

	/**
	 * Dereferences a pointer to a value using the given list of dictionaries to
	 * resolve it in the given scope.
	 * 
	 * @param id
	 *            the pointer id
	 * @param dicts
	 *            a list of dictionaries
	 * @param scope
	 *            the scope to look in
	 * @return the value if could be dereferenced
	 * @throws RuntimeException
	 *             if the dictionaries are empty or the value could not be found
	 */
	public static String dereference(String id, List<Dict> dicts,
			ScopeTypes scope) {
		if (dicts.isEmpty()) {
			throw new RuntimeException(
					"Cannot dereference IDs without dictionaries");
		}
		String dereference = null;
		for (Dict dict : dicts) {
			if (dict.getDefaultScope() == scope) {
				dereference = dict.dereference(id);
				if (dereference != null) {
					return dereference;
				}
			}
		}
		throw new RuntimeException("Could not find dictionary for scope: "
				+ scope);
	}

}
