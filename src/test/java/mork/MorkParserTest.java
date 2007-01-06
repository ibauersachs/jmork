package mork;

import java.io.InputStream;

import junit.framework.AssertionFailedError;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

public class MorkParserTest extends TestCase {

	private MorkParser parser;

	private MockEventListener mockEventListener;

	private ConsoleLoggingEventListener consoleLoggingEventListener;

	@Override
	protected void setUp() throws Exception {
		parser = new MorkParser();
		consoleLoggingEventListener = new ConsoleLoggingEventListener();
		parser.addEventListener(consoleLoggingEventListener);
		mockEventListener = new MockEventListener();
		parser.addEventListener(mockEventListener);
	}

	public void testInvalidSlash() throws Exception {
		try {
			parser.parse("/b");
			fail("Exception expected");
		} catch (Exception expected) {
			assertEquals("Unexpected character at current position: b",
					expected.getMessage());
		}
	}

	public void testGroupCommit() throws Exception {
		parser.parse("@$${50{@bar@$$}50}@");
		assertEvent(mockEventListener, EventType.GROUP_COMMIT, "bar");
	}

	public void testGroupAbort() throws Exception {
		parser.parse("@$${50{@bar@$$}~abort~50}@");
		assertEvent(mockEventListener, EventType.GROUP_ABORT,"50");
	}
	
	public void testComment() throws Exception {
		parser.parse("//test");
		assertEvent(mockEventListener, EventType.COMMENT, "test");
		parser.removeEventListener(mockEventListener);
		parser.removeEventListener(consoleLoggingEventListener);
	}

	public void testEmptyDict() throws Exception {
		parser.parse("<>");
		assertEvent(mockEventListener, EventType.BEGIN_DICT);
		assertEvent(mockEventListener, EventType.END_DICT, "");
	}

	public void testDictWithSingleCell() throws Exception {
		parser.parse("<(foo=bar)>");
		assertEvent(mockEventListener, EventType.BEGIN_DICT);
		assertEvent(mockEventListener, EventType.END_DICT, "(foo=bar)");
	}

	public void testDictWithScopeDefinition() throws Exception {
		parser.parse("<<a=c>(foo=bar)>");
		assertEvent(mockEventListener, EventType.BEGIN_DICT);
		assertEvent(mockEventListener, EventType.BEGIN_DICT_METAINFO);
		assertEvent(mockEventListener, EventType.END_DICT_METAINFO);
		assertEvent(mockEventListener, EventType.END_DICT, "<a=c>(foo=bar)");
	}

	public void testDictWithScopeDefinitionParenthesis() throws Exception {
		parser.parse("<<(a=c)>(foo=bar)>");
		assertEvent(mockEventListener, EventType.BEGIN_DICT);
		assertEvent(mockEventListener, EventType.BEGIN_DICT_METAINFO);
		assertEvent(mockEventListener, EventType.END_DICT_METAINFO);
		assertEvent(mockEventListener, EventType.END_DICT, "<(a=c)>(foo=bar)");
	}

	public void testDictWithLongScopeDefinitionParenthesis() throws Exception {
		parser.parse("<<(atomScope=c)>(foo=bar)>");
		assertEvent(mockEventListener, EventType.BEGIN_DICT);
		assertEvent(mockEventListener, EventType.BEGIN_DICT_METAINFO);
		assertEvent(mockEventListener, EventType.END_DICT_METAINFO);
		assertEvent(mockEventListener, EventType.END_DICT,
				"<(atomScope=c)>(foo=bar)");
	}

	public void testEmptyTable() throws Exception {
		parser.parse("{}");
		assertEvent(mockEventListener, EventType.BEGIN_TABLE);
		assertEvent(mockEventListener, EventType.TABLE, "");
	}

	public void testTableWithMetatable() throws Exception {
		parser.parse("{{}}");
		assertEvent(mockEventListener, EventType.BEGIN_TABLE);
		assertEvent(mockEventListener, EventType.BEGIN_METATABLE);
		assertEvent(mockEventListener, EventType.END_METATABLE, "");
		assertEvent(mockEventListener, EventType.TABLE, "");
	}

	public void testTableWithMetatable2() throws Exception {
		parser.parse("{{(a=b)}}");
		assertEvent(mockEventListener, EventType.BEGIN_TABLE);
		assertEvent(mockEventListener, EventType.BEGIN_METATABLE);
		assertEvent(mockEventListener, EventType.END_METATABLE, "(a=b)");
		assertEvent(mockEventListener, EventType.TABLE, "");
	}

	public void testTableWithMetatableAndData() throws Exception {
		parser.parse("{{(a=b)}(c=d)}");
		assertEvent(mockEventListener, EventType.BEGIN_TABLE);
		assertEvent(mockEventListener, EventType.BEGIN_METATABLE);
		assertEvent(mockEventListener, EventType.END_METATABLE, "(a=b)");
		assertEvent(mockEventListener, EventType.TABLE, "(c=d)");
	}

	public void testRow() throws Exception {
		parser.parse("[ 2 (foo=bar3) ]");
		assertEvent(mockEventListener, EventType.ROW, " 2 (foo=bar3) ");
	}

	public void testCell() throws Exception {
		parser.parse("(foo=bar)");
		assertEvent(mockEventListener, EventType.CELL, "foo=bar");
	}

	public void testParserSimpleExample() throws Exception {
		InputStream is = getClass().getResource("/simple.mab").openStream();
		parser.parse(is);
		assertEvent(mockEventListener, EventType.BEGIN_DICT);
		assertEvent(mockEventListener, EventType.BEGIN_DICT_METAINFO);
		assertEvent(mockEventListener, EventType.END_DICT_METAINFO);
		assertEvent(
				mockEventListener,
				EventType.END_DICT,
				"<(atomScope=c)>(80=cards)(81=dn)(82=modifytimestamp)(83=cn)(84=givenname)(85=mail)(86=xmozillausehtmlmail)(87=sn)");
		assertEvent(mockEventListener, EventType.BEGIN_DICT);
		assertEvent(
				mockEventListener,
				EventType.END_DICT,
				"(90=cn=John Hackworth,mail=jhackworth@atlantis.com)(91=19981001014531Z)(92=John Hackworth)(93=John)(94=jhackworth@atlantis.com)(95=FALSE)(96=Hackworth)");
		assertEvent(mockEventListener, EventType.ROW,
				"1:^80(^81^90)(^82^91)(^83^92)(^84^93)(^85^94)(^86^95)(^87^96)");
	}

	private static void assertEvent(MockEventListener mockEventListener,
			EventType eventType) {
		assertEvent(mockEventListener, eventType, null);
	}

	private static void assertEvent(MockEventListener mockEventListener,
			EventType eventType, String value) {
		Event event = mockEventListener.pop();
		if (event == null) {
			throw new AssertionFailedError(
					"Event expected, but no more events available");
		}
		if (event.eventType != eventType) {
			throw new ComparisonFailure("Wrong Event Type", eventType.name(),
					event.eventType.name());
		}
		if (value == null) {
			if (event.value != null) {
				throw new ComparisonFailure("Wrong Event Value", value,
						event.value);
			}
		} else if (!value.equals(event.value)) {
			throw new ComparisonFailure("Wrong Event Value", value, event.value);
		}
	}

}
