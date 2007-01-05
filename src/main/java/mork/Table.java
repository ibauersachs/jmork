package mork;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Table {

	private String tableId;

	private String scopeName;

	private List<Row> rows = new LinkedList<Row>();

	public Table(String content) {
		this(content, Dict.EMPTY_LIST);
	}

	public Table(String content, List<Dict> dicts) {
		// "{ 1:cards [ 1 (name=Jack) ] [ 2 (name=John)] }"
		content = StringUtils.removeCommentLines(content.trim());
		content = StringUtils.removeNewlines(content);

		// Match table without scope

		Pattern pattern = Pattern
				.compile("\\{\\s*(\\w*):(\\w*)\\s*(\\[.*\\]\\s*)*\\}");
		Matcher matcher = pattern.matcher(content);
		String rowsContent = null;
		if (!matcher.matches()) {
			// Try to match with referenced scope
			Pattern pattern1 = Pattern
					.compile("\\{\\s*(\\w*):\\^([0-9A-Z]*)\\s*(\\[.*\\]\\s*)*\\}");
			Matcher matcher1 = pattern1.matcher(content);
			if (!matcher1.matches()) {
				Pattern pattern0 = Pattern
						.compile("\\{\\s*(\\w*)\\s*(\\[.*\\]\\s*)*\\}");
				Matcher matcher0 = pattern0.matcher(content);
				if (!matcher0.matches()) {
					throw new IllegalArgumentException(
							"Table does not match any of the three alternatives: "
									+ content);
				}
				tableId = matcher0.group(1);
				rowsContent = matcher0.group(2);
			} else {
				tableId = matcher1.group(1);
				scopeName = Dict.dereference("^" + matcher1.group(2), dicts,
						ScopeTypes.COLUMN_SCOPE);
				rowsContent = matcher1.group(3);
			}
		} else {
			tableId = matcher.group(1);
			scopeName = matcher.group(2);
			rowsContent = matcher.group(3);
		}

		Pattern rowsPattern = Pattern.compile("\\[[^\\]]*\\]");
		Matcher rowsMatcher = rowsPattern.matcher(rowsContent);
		while (rowsMatcher.find()) {
			Row row = new Row(rowsMatcher.group(), dicts);
			rows.add(row);
		}
	}

	public String getTableId() {
		return tableId;
	}

	public String getScopeName() {
		return scopeName;
	}

	public List<Row> getRows() {
		return Collections.unmodifiableList(rows);
	}
}
