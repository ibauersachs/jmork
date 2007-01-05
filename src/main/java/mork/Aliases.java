package mork;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Aliases {

	private Map<String, String> dict = new HashMap<String, String>();

	public Aliases(String aliases) {
		this(aliases, Dict.EMPTY_LIST);
	}

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

	public String getValue(String id) {
		return dict.get(id.trim());
	}

	public int count() {
		return dict.size();
	}

	public Map<String, String> getValues() {
		return Collections.unmodifiableMap(dict);
	}

}
