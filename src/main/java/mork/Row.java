package mork;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Row {

	private String rowId;

	private String scopeName;

	private Aliases aliases;

	public Row(String content, List<Dict> dicts) {
		content = StringUtils.removeCommentLines(content);
		content = StringUtils.removeNewlines(content);
		Pattern pattern = Pattern
				.compile("\\s*\\[\\s*(\\w*):(\\^?\\w*)\\s*(.*)\\s*\\]");
		Matcher matcher = pattern.matcher(content);
		if (!matcher.matches()) {
			// Try to match simple row without scope name
			Pattern pattern2 = Pattern
					.compile("\\s*\\[\\s*(\\w*)\\s*(.*)\\s*\\]");
			Matcher matcher2 = pattern2.matcher(content);
			if (!matcher2.matches()) {
				throw new RuntimeException("Row does not match RegEx: "
						+ content);
			}
			rowId = matcher2.group(1);
			String cells = matcher2.group(2);
			aliases = new Aliases(cells, dicts);
		} else {
			rowId = matcher.group(1);

			String scopeValue = matcher.group(2);
			if (scopeValue.startsWith("^")) {
				scopeName = Dict.dereference(scopeValue, dicts,
						ScopeTypes.COLUMN_SCOPE);
			} else {
				scopeName = scopeValue;
			}

			String cells = matcher.group(3);
			aliases = new Aliases(cells, dicts);
		}
	}

	public Row(String content) {
		this(content, Dict.EMPTY_LIST);
	}

	public String getRowId() {
		return this.rowId;
	}

	public String getScopeName() {
		return this.scopeName;
	}

	public String getValue(String id) {
		return aliases.getValue(id);
	}

	public Map<String,String> getValues() {
		return aliases.getValues();
	}

}
