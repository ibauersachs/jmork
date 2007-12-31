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

/**
 * Parses Mork content using event-based parsing.
 * 
 * @author mhaller
 */
public class MorkParser {

	/** Simple logging */
	private Log log = new Log(this);
	
    /** Internal container of event listeners */
    private Collection<EventListener> eventListeners = 
        new LinkedList<EventListener>();

    /** Event reused for all events fired to all event listeners */
    private Event event = new Event();

    /**
     * Ignore exception when they happen within the parsing of mork groups, which
     * is something like a transaction.
     */
	private boolean ignoreTransactionFailures = false;

    /**
     * Adds the listener to the list of listeners notified of Mork events while
     * parsing
     * 
     * @param listener
     *            the listener to add to the list of listeners
     */
    public void addEventListener(EventListener listener) {
        eventListeners.add(listener);
    }

    /**
     * Removes the given listener
     * 
     * @param listener
     *            the listener to remove from the list of listeners being
     *            notified of Mork events while parsing
     */
    public void removeEventListener(EventListener listener) {
        eventListeners.remove(listener);
    }

    /**
     * Fires the given event to all registered event listeners. The event object
     * is reused.
     * 
     * @param eventType
     *            one of {@link EventType}, must not be null
     * @param value
     *            an optional String value, might be null
     */
    private void fireEvent(EventType eventType, String value) {
        event.eventType = eventType;
        event.value = value;
        for (EventListener eventListener: eventListeners) {
            eventListener.onEvent(event);
        }
    }

    /**
     * Convenience method to fire events which have no String content
     * associated.
     * 
     * @param eventType
     *            one of {@link EventType}
     */
    private void fireEvent(EventType eventType) {
        fireEvent(eventType, null);
    }

    /**
     * Parse the given String content
     * 
     * @param morkContent
     *            the content to parse
     */
    public void parse(String morkContent) {
        parse(new StringReader(morkContent));
    }

    /**
     * Parse the given input stream
     * 
     * @param inputStream
     *            an input stream
     */
    public void parse(InputStream inputStream) {
        parse(new InputStreamReader(inputStream));
    }

    /**
     * Parse the given file
     * 
     * @param file
     *            a file
     */
    public void parse(File file) {
        try {
            parse(new FileReader(file));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Parse the Mork content
     * 
     * @param reader
     *            a reader
     */
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

    /**
     * Parses the root elements in a Mork content
     */
    private void parseMain(PushbackReader pis)
        throws IOException {
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
            case '/':
                {
                    int d = pis.read();
                    if (d == '/') {
                        parseComment(pis);
                        break;
                    }
                    throw new RuntimeException("Unexpected character at current position: " + 
                                               (char)d);
                }
            case '@':
                {
                    int d = pis.read();
                    if (d == '$') {
                        int e = pis.read();
                        if (e == '$') {
                            int f = pis.read();
                            if (f == '{') {
                                // Read ID until "{@" appears
                            	if (ignoreTransactionFailures) {
                            		try {
                            			parseGroup(pis);
                            		} catch (RuntimeException exception) {
                            			log.warn("Ignoring parsing error within group",exception);
                            		}
                            	} else {
                            		parseGroup(pis);
                            	}
                                break;
                            }
                            pis.unread(f);
                        }
                        pis.unread(e);
                    }
                    pis.unread(d);
                    break;
                }
            case -1:
                {
                    fireEvent(EventType.END_OF_FILE);
                    return;
                }
            }
        } while (true);
    }

    /**
     * Parses a group, extracts its full content and fires an event.
     * 
     * Note that the id of the Group is lost currently and there is no way of
     * retrieving the id of a Group this way.
     * 
     * @param pis
     * @throws IOException
     */
    private void parseGroup(PushbackReader pis)
        throws IOException {
        String id = parseUntil(pis, "{@");
        String content = parseUntil(pis, "@$$}");
        String abort = parseUntil(pis, id + "}@");
        if ("~abort~".equals(abort)) {
            fireEvent(EventType.GROUP_ABORT, id);
        } else {
            fireEvent(EventType.GROUP_COMMIT, content);
        }
    }

    /**
     * Parse the input as long as the given string is not yet found. Stop if the
     * string is found and return all content read so far.
     * 
     * @param pis
     *            the input stream, which is positioned after the
     *            <code>string</code>
     * @param string
     *            the content to look for to stop reading
     * @return the content parsed, without the content of the
     *         <code>string</code> parameter which is silently removed
     * @throws IOException
     */
    private String parseUntil(PushbackReader pis, String string)
        throws IOException {
        final StringBuffer buf = new StringBuffer();
        while (true) {
            int c = pis.read();
            if (c == '\r' || c == '\n' || c == -1) {
                continue;
            }
            buf.append((char)c);
            if (buf.toString().endsWith(string)) {
                buf.delete(buf.length() - string.length(), buf.length());
                break;
            }
        }
        return buf.toString();
    }

    /**
     * Parses a Mork Table which can also contain metatables. All other content
     * is given back in the event fired.
     * 
     * @param pis
     * @throws IOException
     */
    private void parseTable(PushbackReader pis)
        throws IOException {
        fireEvent(EventType.BEGIN_TABLE);
        final StringBuffer buffer = new StringBuffer();
        final StringBuffer metaTableBuffer = new StringBuffer();
        boolean inMetaTable = false;
        do {
            int c = pis.read();
            switch (c) {
            case '{':
                {
                    inMetaTable = true;
                    fireEvent(EventType.BEGIN_METATABLE);
                    break;
                }
            case '}':
                {
                    if (inMetaTable) {
                        fireEvent(EventType.END_METATABLE, 
                                  metaTableBuffer.toString());
                        inMetaTable = false;
                        break;
                    }
                    fireEvent(EventType.TABLE, buffer.toString());
                    return;
                }
            default:
                {
                    if (c == '\r' || c == '\n' || c == -1) {
                        break;
                    }
                    if (inMetaTable) {
                        metaTableBuffer.append((char)c);
                    } else {
                        buffer.append((char)c);
                    }
                    break;
                }
            }
        } while (true);
    }

    /**
     * Parses a Mork Cell until its closed and fires an event with the content
     * of the cell. The event does not contain the opening and closing brackets,
     * which are silently ignored.
     * 
     * @param pis
     * @throws IOException
     */
    private void parseCell(PushbackReader pis)
        throws IOException {
        final StringBuffer buffer = new StringBuffer();
        do {
            int c = pis.read();
            switch (c) {
            case '\\':
                int escapedCharacter = pis.read();
                if (escapedCharacter == -1) {
                    throw new IOException("Escape character must not be last character in file");
                }
                buffer.append((char)escapedCharacter);
                break;
            case ')':
                fireEvent(EventType.CELL, buffer.toString());
                return;
            }
            if (c == '\r' || c == '\n' || c == -1) {
                break;
            }
            buffer.append((char)c);
        } while (true);
    }

    /**
     * Parses a row and fires an event when the row is finished. The event does
     * not include the open and closing brackets.
     * 
     * @param pis
     * @throws IOException
     */
    private void parseRow(PushbackReader pis)
        throws IOException {
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
            buffer.append((char)c);
        } while (true);
    }

    /**
     * Read until first encounter of newline character. Consume all newline
     * characters.
     * 
     * @param pis
     * @throws IOException
     */
    private void parseComment(PushbackReader pis)
        throws IOException {
        final StringBuffer buffer = new StringBuffer();
        do {
            int c = pis.read();
            if (c == '\r' || c == '\n' || c == -1) {
                break;
            }
            buffer.append((char)c);
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

    /**
     * Parse a whole dictionary.
     * 
     * The <code>escaped</code> flag denotes if a special character is ignored
     * as command and thus read as a value object, because it has been escaped
     * using a backslash previsouly.
     * 
     * @param pis
     * @throws IOException
     */
    private void parseDict(final PushbackReader pis)
        throws IOException {
        fireEvent(EventType.BEGIN_DICT);
        final StringBuffer buffer = new StringBuffer();
        boolean inMetaDict = false;
        boolean isInCell = false;
        do {
            int c = pis.read();
            switch (c) {
            case -1:
                {
                    fireEvent(EventType.END_DICT, buffer.toString());
                    return;
                }
            case '<':
                fireEvent(EventType.BEGIN_DICT_METAINFO);
                inMetaDict = true;
                buffer.append((char)c);
                break;
            case '>':
                if (inMetaDict) {
                    fireEvent(EventType.END_DICT_METAINFO);
                    inMetaDict = false;
                    buffer.append((char)c);
                    break;
                }
                fireEvent(EventType.END_DICT, buffer.toString());
                return;
            case '(':
                if (!isInCell) {
                    isInCell = true;
                }
                buffer.append((char)c);
                break;
            case ')':
                if (isInCell) {
                    isInCell = false;
                }
                buffer.append((char)c);
                break;
            case '/':
                if (isInCell) {
                    buffer.append((char)c);
                    break;
                }
                int d = pis.read();
                if (d == '/') {
                    parseComment(pis);
                    break;
                }
                buffer.append((char)c);
                break;
            default:
                if (c == '\r' || c == '\n') {
                    break;
                }
                buffer.append((char)c);
                break;
            }
        } while (true);
    }

    /**
     * If set to true, the mork parser ignores exceptions when they happen within
     * the parsing of groups, as they are often non-essential for reading address book information.
     * 
     * @param ignoreTransactionFailures
     */
	public void setIgnoreTransactionFailures(boolean ignoreTransactionFailures) {
		this.ignoreTransactionFailures = ignoreTransactionFailures;
	}

}
