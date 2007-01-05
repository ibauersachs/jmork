package mork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedList;

public class MorkParser {

	private Collection<EventListener> eventListeners = new LinkedList<EventListener>();

	private Event event = new Event();

	public void addEventListener(EventListener listener) {
		eventListeners.add(listener);
	}

	public void removeEventListener(EventListener listener) {
		eventListeners.remove(listener);
	}

	private void fireEvent(EventType eventType, String value) {
		event.eventType = eventType;
		event.value = value;
		for (EventListener eventListener : eventListeners) {
			eventListener.onEvent(event);
		}
	}

	private void fireEvent(EventType eventType) {
		fireEvent(eventType, null);
	}

	public void parse(String morkContent) {
		parse(new StringReader(morkContent));
	}

	public void parse(InputStream inputStream) {
		parse(new InputStreamReader(inputStream));
	}

	public void parse(File file) {
		try {
			parse(new FileReader(file));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void parse(Reader reader) {
		try {
			PushbackReader pis = new PushbackReader(reader, 8);
			parseMain(pis);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	// < - open angle - begins a dict (inside a dict, begins metainfo row)
	// > - close angle - ends a dict
	// [ - open bracket - begins a row (inside a row, begins metainfo row)
	// ] - close bracket - ends a row
	// { - open brace - begins a table (inside a table, begins metainfo row)
	// } - close brace - ends a table
	// ( - open paren - begins a cell
	// ) - close paren - ends a cell

	private void parseMain(PushbackReader pis) throws IOException {
		do {
			int c = pis.read();
			switch (c) {
			case '<':
				parseDict(pis);
				break;
			case '[':
				parseRow(pis);
				break;
			case '(':
				parseCell(pis);
				break;
			case '{':
				parseTable(pis);
				break;
			case '/': {
				int d = pis.read();
				if (d == '/') {
					parseComment(pis);
					break;
				}
				throw new RuntimeException(
						"Unexpected character at current position: " + (char) d);
			}
			case '@': {
				int d = pis.read();
				if (d == '$') {
					int e = pis.read();
					if (e == '$') {
						int f = pis.read();
						if (f == '{') {
							// Read ID until "{@" appears
							parseGroup(pis);
							break;
						}
						pis.unread(f);
					}
					pis.unread(e);
				}
				pis.unread(d);
				break;
			}
			case -1: {
				fireEvent(EventType.END_OF_FILE);
				return;
			}
			}
		} while (true);
	}

	private void parseGroup(PushbackReader pis) throws IOException {
		String id = parseUntil(pis, "{@");
		String content = parseUntil(pis, "@$$}");
		String abort = parseUntil(pis, id + "}@");
		if ("~abort~".equals(abort)) {
			fireEvent(EventType.GROUP_ABORT, id);
		} else {
			fireEvent(EventType.GROUP_COMMIT, content);
		}
	}

	private String parseUntil(PushbackReader pis, String string)
			throws IOException {
		final StringBuffer buf = new StringBuffer();
		while (true) {
			int c = pis.read();
			if (c == '\r' || c == '\n' || c == -1) {
				continue;
			}
			buf.append((char) c);
			if (buf.toString().endsWith(string)) {
				buf.delete(buf.length() - string.length(), buf.length());
				break;
			}
		}
		return buf.toString();
	}

	private void parseTable(PushbackReader pis) throws IOException {
		fireEvent(EventType.BEGIN_TABLE);
		final StringBuffer buffer = new StringBuffer();
		final StringBuffer metaTableBuffer = new StringBuffer();
		boolean inMetaTable = false;
		do {
			int c = pis.read();
			switch (c) {
			case '{': {
				inMetaTable = true;
				fireEvent(EventType.BEGIN_METATABLE);
				break;
			}
			case '}': {
				if (inMetaTable) {
					fireEvent(EventType.END_METATABLE, metaTableBuffer
							.toString());
					inMetaTable = false;
					break;
				}
				fireEvent(EventType.TABLE, buffer.toString());
				return;
			}
			default: {
				if (c == '\r' || c == '\n' || c == -1) {
					break;
				}
				if (inMetaTable) {
					metaTableBuffer.append((char) c);
				} else {
					buffer.append((char) c);
				}
				break;
			}
			}
		} while (true);
	}

	private void parseCell(PushbackReader pis) throws IOException {
		final StringBuffer buffer = new StringBuffer();
		do {
			int c = pis.read();
			switch (c) {
			case ')':
				fireEvent(EventType.CELL, buffer.toString());
				return;
			}
			if (c == '\r' || c == '\n' || c == -1) {
				break;
			}
			buffer.append((char) c);
		} while (true);
	}

	private void parseRow(PushbackReader pis) throws IOException {
		final StringBuffer buffer = new StringBuffer();
		do {
			int c = pis.read();
			switch (c) {
			case ']':
				fireEvent(EventType.ROW, buffer.toString());
				return;
			}
			if (c == '\r' || c == '\n' || c == -1) {
				break;
			}
			buffer.append((char) c);
		} while (true);
	}

	/**
	 * Read until first encounter of newline character. Consume all newline
	 * characters.
	 * 
	 * @param pis
	 * @throws IOException
	 */
	private void parseComment(PushbackReader pis) throws IOException {
		final StringBuffer buffer = new StringBuffer();
		do {
			int c = pis.read();
			if (c == '\r' || c == '\n' || c == -1) {
				break;
			}
			buffer.append((char) c);
		} while (true);
		// Now consume all newlines
		do {
			int c = pis.read();
			if (c == -1) {
				fireEvent(EventType.COMMENT, buffer.toString());
				return;
			}
			if (c != '\r' && c != '\n') {
				pis.unread(c);
				fireEvent(EventType.COMMENT, buffer.toString());
				return;
			}
		} while (true);
	}

	private void parseDict(PushbackReader pis) throws IOException {
		fireEvent(EventType.BEGIN_DICT);
		final StringBuffer buffer = new StringBuffer();
		boolean inMetaDict = false;
		do {
			int c = pis.read();
			switch (c) {
			case -1: {
				fireEvent(EventType.END_DICT, buffer.toString());
				return;
			}
			case '<':
				fireEvent(EventType.BEGIN_DICT_METAINFO);
				inMetaDict = true;
				buffer.append((char) c);
				break;
			case '>':
				if (inMetaDict) {
					fireEvent(EventType.END_DICT_METAINFO);
					inMetaDict = false;
					buffer.append((char) c);
					break;
				}
				fireEvent(EventType.END_DICT, buffer.toString());
				return;
			case '/':
				int d = pis.read();
				if (d == '/') {
					parseComment(pis);
					break;
				}
				buffer.append((char) c);
				break;
			default:
				if (c == '\r' || c == '\n') {
					break;
				}
				buffer.append((char) c);
				break;
			}
		} while (true);
	}

}
