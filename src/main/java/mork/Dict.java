package mork;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dict {

	/** A typed empty list of dictionaries */
	public static final List<Dict> EMPTY_LIST = new ArrayList<Dict>(0);

	private String scopeName;

	private String scopeValue;

	private Aliases aliases;

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

	public ScopeTypes getDefaultScope() {
		if (scopeValue != null
				&& scopeValue.toLowerCase(Locale.getDefault()).startsWith("c")) {
			return ScopeTypes.COLUMN_SCOPE;
		}
		return ScopeTypes.ATOM_SCOPE;
	}

	public String getScopeName() {
		return scopeName;
	}

	public String getScopeValue() {
		return scopeValue;
	}

	public String getValue(String id) {
		return aliases.getValue(id);
	}

	public int getAliasCount() {
		return aliases.count();
	}

	/**
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
