package mork;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Row can be located either in the root of Mork files or in tables. A Row
 * contains multiple values and has a row identifier associated. A row can also
 * be scoped.
 * 
 * @author mhaller
 */
public class Row {

    /** The identifier of the row */
    private String rowId;

    /** An optional scope of the row */
    private String scopeName;

    /** The parsed values of the cells in the Row */
    private Aliases aliases;

    /**
     * Parse a Row in the given content (must include the brackets) and resolves
     * references using the given dictionaries.
     * 
     * The Row can optionally have a scope.
     * 
     * @param content
     *            the Mork content of a Row including opening and closing
     *            brackets.
     * @param dicts
     *            a list of dictionaries
     */
    public Row(String content, List<Dict> dicts) {
        content = StringUtils.removeCommentLines(content);
        content = StringUtils.removeNewlines(content);
        Pattern pattern = 
            Pattern.compile("\\s*\\[\\s*(\\w*):(\\^?\\w*)\\s*(.*)\\s*\\]");
        Matcher matcher = pattern.matcher(content);
        if (!matcher.matches()) {
        	Pattern pattern3 = Pattern.compile("\\[\\-([0-9A-F]*)\\]");
			Matcher matcher3 = pattern3.matcher(content);
			if (matcher3.matches()) {
				// Row without cells (row within a transaction, e.g. for removal)
				rowId = matcher3.group(1);
				aliases = new Aliases();
			} else {
				// Try to match simple row without scope name
				Pattern pattern2 = Pattern
						.compile("\\s*\\[\\s*(-?\\w*)\\s*(.*)\\s*\\]");
				Matcher matcher2 = pattern2.matcher(content);
				if (!matcher2.matches()) {
					throw new RuntimeException("Row does not match RegEx: "
							+ content);
				}
				rowId = matcher2.group(1);
				String cells = matcher2.group(2);
				aliases = new Aliases(cells, dicts);
			}
        } else {
            rowId = matcher.group(1);

            String scopeValue = matcher.group(2);
            if (scopeValue.startsWith("^")) {
                scopeName = 
                        Dict.dereference(scopeValue, dicts, ScopeTypes.COLUMN_SCOPE);
            } else {
                scopeName = scopeValue;
            }

            String cells = matcher.group(3);
            aliases = new Aliases(cells, dicts);
        }
    }

    /**
     * Parse a Row in the given content (must include the brackets). Does not
     * resolve references as no dictionaries are given. The Row can optionally
     * have a scope.
     * 
     * @param content
     *            the Mork content of a Row including opening and closing
     *            brackets.
     */
    public Row(String content) {
        this(content, Dict.EMPTY_LIST);
    }

    /**
     * Returns the identifier of the row, usually a numeric value
     * 
     * @return the identifier of the row
     */
    public String getRowId() {
        return this.rowId;
    }

    /**
     * Returns an optional scope of the row, might be <code>null</code>.
     * 
     * @return the scope of the row if defined, or <code>null</code>
     */
    public String getScopeName() {
        return this.scopeName;
    }

    /**
     * Returns the value of a cell with the given id. The id must already be
     * dereferenced.
     * 
     * @param id
     *            the id of the cell
     * @return the dereferenced literal value of the Cell with the given id
     */
    public String getValue(String id) {
        return aliases.getValue(id);
    }

    /**
     * Returns a Map of all values found in the Row
     * 
     * @return a Map of all values found in the Row. The column header names
     *         (ids) and the values are already dereferenced.
     */
    public Map<String, Alias> getAliases() {
        return aliases.getAliases();
    }

  	/**
  	 * access to this row's keySet
  	 * @return
  	 */
  	public Set<String> getKeySet() {
  		return aliases.getKeySet();
  	}

}
