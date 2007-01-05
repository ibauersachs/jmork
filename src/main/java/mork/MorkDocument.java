package mork;

import java.io.Reader;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

public class MorkDocument implements EventListener {

	private List<Dict> dicts = new LinkedList<Dict>();

	private List<Row> rows = new LinkedList<Row>();

	private List<Table> tables = new LinkedList<Table>();

	public MorkDocument(Reader reader) {
		MorkParser parser = new MorkParser();
		parser.addEventListener(this);
		parser.parse(reader);
	}

	public void onEvent(Event event) {
		switch (event.eventType) {
		case END_DICT: {
			Dict dict = new Dict("<" + event.value + ">");
			dicts.add(dict);
			break;
		}
		case ROW: {
			Row row = new Row("[" + event.value + "]", dicts);
			rows.add(row);
			break;
		}
		case TABLE: {
			Table table = new Table("{" + event.value + "}", dicts);
			tables.add(table);
			break;
		}
		case GROUP_COMMIT: {
			MorkParser parser = new MorkParser();
			parser.addEventListener(this);
			parser.parse(new StringReader(event.value));
			break;
		}
		case END_METATABLE:
		case BEGIN_TABLE:
		case BEGIN_METATABLE:
		case BEGIN_DICT:
		case BEGIN_DICT_METAINFO:
		case END_DICT_METAINFO:
		case END_OF_FILE:
		case COMMENT:
			break;
		default:
			throw new RuntimeException("Unimplemented event: "
					+ event.eventType);
		}
	}

	public List<Dict> getDicts() {
		return dicts;
	}

	public List<Row> getRows() {
		return rows;
	}

	public List<Table> getTables() {
		return tables;
	}
}
