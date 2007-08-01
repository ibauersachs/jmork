package mork;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses a Mork table, which has a table identifier and contains multiple rows.
 * 
 * @author mhaller
 */
public class Table {

    /** The (probably numeric) table identifier */
    private String tableId;

    /** An optional scope for the table */
    private String scopeName;

    /** Internal container for rows found within the Table definition */
    private List<Row> rows = new LinkedList<Row>();

    /**
     * Parses a new Mork Table.
     * 
     * As no dictionaries are given, the Mork Table must not use any references.
     * 
     * @param content
     */
    public Table(String content) {
        this(content, Dict.EMPTY_LIST);
    }

    /**
     * Parses a new Mork Table and resolves any references to literal values
     * using the given list of dictionaries.
     * 
     * @param content
     *            the Mork content to parse
     * @param dicts
     *            a list of Dictionaries to resolve literal values.
     */
    public Table(String content, List<Dict> dicts) {
        // "{ 1:cards [ 1 (name=Jack) ] [ 2 (name=John)] }"
        //		content = StringUtils.removeCommentLines(content.trim());
        //		content = StringUtils.removeNewlines(content);

        // Match table without scope

        Pattern pattern = 
            Pattern.compile("\\{\\s*([-\\w]*):(\\w*)\\s*(\\[.*\\]\\s*)*\\}");
        Matcher matcher = pattern.matcher(content);
        String rowsContent = null;
        if (!matcher.matches()) {
            // Try to match with referenced scope
            Pattern pattern1 = 
                Pattern.compile("\\{\\s*([-\\w]*):\\^([0-9A-Z]*)\\s*(\\[.*\\]\\s*)*\\}");
            Matcher matcher1 = pattern1.matcher(content);
            if (!matcher1.matches()) {
                Pattern pattern0 = 
                    Pattern.compile("\\{\\s*([-\\w]*)\\s*(\\[.*\\]\\s*)*\\}");
                Matcher matcher0 = pattern0.matcher(content);
                if (!matcher0.matches()) {
                    throw new IllegalArgumentException("Table does not match any of the three alternatives: " + 
                                                       content);
                }
                tableId = matcher0.group(1);
                rowsContent = matcher0.group(2);
            } else {
                tableId = matcher1.group(1);
                scopeName = 
                        Dict.dereference("^" + matcher1.group(2), dicts, ScopeTypes.COLUMN_SCOPE);
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

    /**
     * Returns the (probably numeric) table identifier
     * 
     * @return the table identifier
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * Returns the optional scope of the table, or <code>null</code>
     * 
     * @return the scope of the table, if found in the table definition, or
     *         <code>null</code>
     */
    public String getScopeName() {
        return scopeName;
    }

    /**
     * Returns an unmodifiable list of Mork Rows
     * 
     * @return an unmodifiable list of Mork Rows, might be empty but never
     *         <code>null</code>
     */
    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }
}
