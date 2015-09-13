package mork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * writes a MorkDocument to output according to https://wiki.mozilla.org/Mork
 * http://www-archive.mozilla.org/mailnews/arch/mork/grammar.txt
 * 
 * @author wf
 * 
 */
public class MorkWriter {
	MorkDocument currentDocument;

	OutputStreamWriter writer;

	public enum OutputStyle {
		terse, humandReadable, formatted
	};

	OutputStyle outputStyle = OutputStyle.formatted;

	int indentation = 0;

	int tabSize = 2;

	/**
	 * @return the tabSize
	 */
	public int getTabSize() {
		return tabSize;
	}

	/**
	 * @param tabSize
	 *          the tabSize to set
	 */
	public void setTabSize(int tabSize) {
		this.tabSize = tabSize;
	}

	/**
	 * @return the outputStyle
	 */
	public OutputStyle getOutputStyle() {
		return outputStyle;
	}

	/**
	 * @param outputStyle
	 *          the outputStyle to set
	 */
	public void setOutputStyle(OutputStyle outputStyle) {
		this.outputStyle = outputStyle;
	}

	/**
	 * indent the output
	 */
	protected void indent() {
		indentation += tabSize;
	}

	/**
	 * unindent the output
	 */
	protected void unindent() {
		indentation -= tabSize;
	}

	// the magic string
	public static final String zm_Magic = "//<!-- <mdb:mork:z v=\"1.4\"/> -->";

	/**
	 * zm:LineEnd ::= #xA #xD | #xD #xA | #xA | #xD / 1 each if possible /
	 */
	public static final String zm_LineEnd = "\n";

	/**
	 * zm:S ::= (#x20 | #x9 | #xA | #xD | zm:Continue | zm:Comment)+ / space /
	 */
	public static final String zm_Space = " ";

	/**
	 * @return the writer
	 */
	public OutputStreamWriter getWriter() {
		return writer;
	}

	/**
	 * @param writer
	 *          the writer to set
	 */
	public void setWriter(OutputStreamWriter writer) {
		this.writer = writer;
	}

	/**
	 * @return the currentDocument
	 */
	public MorkDocument getCurrentDocument() {
		return currentDocument;
	}

	/**
	 * @param currentDocument
	 *          the currentDocument to set
	 */
	public void setCurrentDocument(MorkDocument currentDocument) {
		this.currentDocument = currentDocument;
	}

	/**
	 * create a MorkWriter for a given MorkDocument
	 * 
	 * @param document
	 */
	public MorkWriter(MorkDocument document) {
		this.currentDocument = document;
	}

	/**
	 * write the Indentation depending on the outputStyle
	 * 
	 * @throws IOException
	 */
	public void writeIndent() throws IOException {
		writer.append(zm_LineEnd);
		switch (outputStyle) {
			case formatted:
				for (int i = 0; i < indentation; i++) {
					writer.append(zm_Space);
				}
				break;
			case humandReadable:
			case terse:
			default:
				break;
		}
	}

	/**
	 * write to the given file
	 * 
	 * @param file
	 *          a file
	 */
	public void write(File file) {
		try {
			write(new FileWriter(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * write to the given output stream
	 * 
	 * @param outputStream
	 *          an output stream
	 * @throws IOException
	 */
	public void write(OutputStream outputStream) throws IOException {
		write(new OutputStreamWriter(outputStream));
	}

	/**
	 * write to the given OutputStreamWriter
	 * 
	 * @param outputStreamWriter
	 * @throws IOException
	 */
	public void write(OutputStreamWriter outputStreamWriter) throws IOException {
		this.writer = outputStreamWriter;
		writeDocument();
	}

	/**
	 * write a Mork document
	 * 
	 * @throws IOException
	 */
	public void writeDocument() throws IOException {
		assert (currentDocument != null);
		assert (writer != null);
		writeHeader();
		writeContents();
		writer.close();
	}

	/**
	 * write Contents - that is the multiple zm:Content | zm:Group zm:Start ::=
	 * zm:Magic zm:LineEnd zm:Header (zm:Content | zm:Group)
	 * 
	 * @throws IOException
	 * 
	 */
	protected void writeContents() throws IOException {
		// TODO allow for multiple contents according to grammar
		writeContent();
		writeGroup();
	}

	/**
	 * write Content zm:Content ::= (zm:Dict | zm:Table | zm:Update)
	 * 
	 * @throws IOException
	 */
	protected void writeContent() throws IOException {
		List<Dict> dicts = currentDocument.getDicts();
		List<Table> tables = currentDocument.getTables();
		for (int i = 0; i < Math.max(dicts.size(), tables.size()); i++) {
			if (i < dicts.size())
				writeDict(dicts.get(i));
			if (i < tables.size())
				writeTable(tables.get(i));
		}
	}

	/**
	 * write the given table 
	 * zm:Table ::= zm:S? '{' zm:S? zm:Id zm:TableItem zm:S? '}'
	 * 
	 * @param table
	 * @throws IOException
	 */
	protected void writeTable(Table table) throws IOException {
		indent();
		writeIndent();
		writer.append('{');
		writeId(table.getTableId());
		writer.append(':');
		indent();
		writeIndent();
		List<Row> rows = table.getRows();
		for (Row row : rows) {
			writeRow(row);
		}
		unindent();
		writeIndent();
		writer.append('}');
		unindent();
	}

	/**
	 * write the given Row zm:Row ::= zm:S? '[' zm:S? zm:Id zm:RowItem* zm:S? ']'
	 * 
	 * @param row
	 * @throws IOException
	 */
	protected void writeRow(Row row) throws IOException {
		indent();
		writeIndent();
		writer.append('[');
		writeId(row.getRowId());
		Map<String, Alias> aliases = row.getAliases();
		for (String id : aliases.keySet()) {
			writeRowItem(id, aliases.get(id));
		}
		//writeIndent();
		writer.append(']');
		unindent();
	}

	/**
	 * write the given row item
	 * 
	 * @param name
	 * @param alias
	 *          zm:RowItem ::= zm:MetaRow | zm:Cell 
	 *          zm:MetaRow ::= zm:S? '[' zm:S?  zm:Cell* zm:S? ']' / meta attributes /
	 *          zm:Cell    ::= zm:S? '(' zm:Column zm:S? zm:Slot? ')' 
	 *          zm:Column  ::= zm:S? (zm:Name | zm:ValueRef) 
	 *          zm:Slot    ::= zm:Value | zm:AnyRef   zm:S?
	 *          zm:Value   ::= '=' ([^)] | '\' zm:NonCRLF | zm:Continue |
	 *          zm:Dollar)* / content ')', '\', and '$' must be quoted with '\'
	 *          inside zm:Value /
	 *          zm:ValueRef  ::= zm:S? '^' zm:Id / use '^' to avoid zm:Name ambiguity /
	 * @throws IOException
	 */
	protected void writeRowItem(String name, Alias alias) throws IOException {
		//indent();
		//writeIndent();
		writer.append('(');
		if (alias.getRefId().startsWith("^")){
			writer.append(alias.getRefId());
		} else {
			writeName(name);
		}
		if (alias.getValueRef()!=null) {
			writer.append(alias.getValueRef());
		} else {
			writeValue(alias.getValue());
		}	
		writer.append(')');
		//unindent();
	}

	/**
	 * write the given Dictionary
	 * zm:Dict      ::= zm:S? '&lt;' zm:DictItem* zm:S? '&gt;'
	 * zm:DictItem  ::= zm:MetaDict | zm:Alias
	 * zm:MetaDict  ::= zm:S? '&lt;' zm:S? zm:Cell* zm:S? '&gt;' / meta attributes /
	 * zm:Alias     ::= zm:S? '(' zm:Id zm:S? zm:Value ')'
	 * @param dict
	 * @throws IOException 
	 */
	protected void writeDict(Dict dict) throws IOException {
		indent();
		writeIndent();
		writer.append('<');
		indent();
		Aliases aliases = dict.getAliases();
		Set<String> keys = aliases.getKeySet();
		for (String key : keys) {
			writeAlias(key, aliases.getAlias(key));
		}
		unindent();
		writeIndent();
		writer.append('>');
		unindent();
	}

	/**
	 * write the given alias (key / value pairs) 
	 * zm:Alias ::= zm:S? '(' zm:Id zm:S? zm:Value ')'
	 * 
	 * @param key
	 *          - the id of the alias
	 * @param alias
	 *          - the value of alias
	 * @throws IOException
	 */
	protected void writeAlias(String key, Alias alias) throws IOException {
		writeIndent();
		writer.append('(');
		writeId(key);
		writeValue(alias.getValue());
		writer.append(')');
	}

	/**
	 * write a value zm:Value ::= '=' ([^)] | '\' zm:NonCRLF | zm:Continue |
	 * zm:Dollar)*
	 * 
	 * @param value
	 * @throws IOException
	 */
	private void writeValue(String value) throws IOException {
		writer.append('=');
		writer.append(value);
	}
	
	/**
	 * write the given name
	 * zm:Name      ::= [a-zA-Z:_] zm:MoreName*
	 * zm:MoreName  ::= [a-zA-Z:_+-?!]
	 * / names only need to avoid space and '^', so this is more limiting /
	 * @param name
	 * @throws IOException 
	 */
	protected void writeName(String name) throws IOException {
		writer.append(name);
	}

	/**
	 * zm:Id ::= zm:Hex+ / a row, table, or value id is naked hex /
	 * 
	 * @param key
	 * @throws IOException
	 */
	private void writeId(String key) throws IOException {
		writer.append(key);
	}

	/**
	 * write a group
	 */
	protected void writeGroup() {
		// TODO implement

	}

	/**
	 * write a Mork 1.4 header zm:Magic zm:LineEnd zm:Header
	 * 
	 * @throws IOException
	 */
	protected void writeHeader() throws IOException {
		writer.append(zm_Magic);
		writer.append(zm_LineEnd);
		// TODO write header
	}

}
