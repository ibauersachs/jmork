package mork;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class DictTest extends TestCase {

	public void testInvalid() throws Exception {
		try {
			new Dict("test");
			fail("Exception expected");
		} catch (Exception expected) {
			assertEquals("RegEx does not match: test", expected.getMessage());
		}
	}

	public void testLearnPattern1() throws Exception {
		Pattern pattern = Pattern.compile("<(<.*>)?(.*)>");
		Matcher matcher = pattern.matcher("<<a=c>test>");
		assertTrue(matcher.matches());
		assertEquals("<<a=c>test>", matcher.group(0));
		assertEquals("<a=c>", matcher.group(1));
		assertEquals("test", matcher.group(2));
	}

	public void testLearnPattern2() throws Exception {
		// Pattern pattern =
		// Pattern.compile("<\\s*<\\(a=c\\)>\\s*(?:\\/\\/)?\\s*(\\(.+?\\))\\s*>");
		Pattern pattern = Pattern.compile("(?:\\(\\w*=[^\\)]*\\))");
		{
			Matcher matcher = pattern.matcher("");
			assertFalse(matcher.find());
		}
		{
			Matcher matcher = pattern.matcher("(80=foo)");
			assertTrue(matcher.find());
			assertEquals("(80=foo)", matcher.group());
			assertFalse(matcher.find());
		}
		{
			Matcher matcher = pattern.matcher("(80=foo)(81=bar)");
			assertTrue(matcher.find());
			assertEquals("(80=foo)", matcher.group());
			assertTrue(matcher.find());
			assertEquals("(81=bar)", matcher.group());
			assertFalse(matcher.find());
		}
		{
			Matcher matcher = pattern.matcher("(80=foo)(81=bar)(82=foobar)");
			assertTrue(matcher.find());
			assertEquals("(80=foo)", matcher.group());
			assertTrue(matcher.find());
			assertEquals("(81=bar)", matcher.group());
			assertTrue(matcher.find());
			assertEquals("(82=foobar)", matcher.group());
			assertFalse(matcher.find());
		}
	}

	public void testAlias() throws Exception {
		Dict dict = new Dict("<(B8=Custom3)>");
		assertEquals("Custom3", dict.getValue("B8"));
	}

	public void testEmpty() throws Exception {
		Dict dict = new Dict("<>");
		assertSame(ScopeTypes.ATOM_SCOPE, dict.getDefaultScope());
		assertEquals(0, dict.getAliasCount());
	}

	public void testOnlyDefaultScopeDefinition() throws Exception {
		Dict dict = new Dict("<<a=c>>");
		assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
		assertEquals(0, dict.getAliasCount());
		assertEquals("a", dict.getScopeName());
		assertEquals("c", dict.getScopeValue());
	}

	public void testSingleAlias() throws Exception {
		Dict dict = new Dict("<<a=c>(80=foo)>");
		assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
		assertEquals(1, dict.getAliasCount());
		assertEquals("foo", dict.getValue("80"));
		assertEquals("a", dict.getScopeName());
		assertEquals("c", dict.getScopeValue());
	}

	public void testDictWithNewlineValue() throws Exception {
        Dict dict = new Dict("<<a=c>(80=foo\r\nbar)>");
        assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
        assertEquals(1, dict.getAliasCount());
        assertEquals("foo\r\nbar", dict.getValue("80"));
        assertEquals("a", dict.getScopeName());
        assertEquals("c", dict.getScopeValue());
	}

	public void testSingleAliasWithInnerWhitespaces() throws Exception {
		Dict dict = new Dict("< <a=c> (80=foo) >");
		assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
		assertEquals(1, dict.getAliasCount());
		assertEquals("foo", dict.getValue("80"));
		assertEquals("a", dict.getScopeName());
		assertEquals("c", dict.getScopeValue());
	}

	public void testSingleAliasWithInnerAndOuterWhitespaces() throws Exception {
		Dict dict = new Dict(" < <a=c> (80=foo) > ");
		assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
		assertEquals(1, dict.getAliasCount());
		assertEquals("foo", dict.getValue("80"));
		assertEquals("a", dict.getScopeName());
		assertEquals("c", dict.getScopeValue());
	}

	public void testSingleAliasWithOuterWhitespaces() throws Exception {
		Dict dict = new Dict(" <<a=c>(80=foo)> ");
		assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
		assertEquals(1, dict.getAliasCount());
		assertEquals("foo", dict.getValue("80"));
		assertEquals("a", dict.getScopeName());
		assertEquals("c", dict.getScopeValue());
	}

	public void testDefaultScopeColumnScope() throws Exception {
		Dict dict = new Dict("<<a=c>(80=foo)>");
		assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
		assertEquals("a", dict.getScopeName());
		assertEquals("c", dict.getScopeValue());
	}

	public void testDefaultScopeAtomScope() throws Exception {
		Dict dict = new Dict("<<a=a>(80=foo)>");
		assertSame(ScopeTypes.ATOM_SCOPE, dict.getDefaultScope());
		assertEquals("a", dict.getScopeName());
		assertEquals("a", dict.getScopeValue());
	}

	public void testDictComplex() throws Exception {
		Dict dict = new Dict(
				"<<(atomScope=c)>(80=cards)(81=dn)(82=modifytimestamp)(83=cn)(84=givenname)(85=mail)(86=xmozillausehtmlmail)(87=sn)>");
		assertEquals("atomScope", dict.getScopeName());
		assertEquals("c", dict.getScopeValue());
		assertSame(ScopeTypes.COLUMN_SCOPE, dict.getDefaultScope());
		assertEquals(8, dict.getAliasCount());
		assertEquals("cards", dict.getValue("80"));
		assertEquals("dn", dict.getValue("81"));
		assertEquals("modifytimestamp", dict.getValue("82"));
		assertEquals("cn", dict.getValue("83"));
		assertEquals("givenname", dict.getValue("84"));
		assertEquals("mail", dict.getValue("85"));
		assertEquals("xmozillausehtmlmail", dict.getValue("86"));
		assertEquals("sn", dict.getValue("87"));
	}

	public void testDictWithComment() throws Exception {
		String content = StringUtils.fromResource("/DictTestWithCharset.txt");
		content = StringUtils.removeCommentLines(content);
		content = StringUtils.removeNewlines(content);
		Dict dict = new Dict(content);
		assertEquals(12, dict.getAliasCount());
		assertEquals("Typed", dict.getValue("8A"));
		assertEquals("Hidden", dict.getValue("89"));
		assertEquals("ns:history:db:row:scope:history:all", dict.getValue("80"));
	}

	public void testDictSinglepartDict() throws Exception {
		String content = StringUtils.fromResource("/singlepart.dict.txt");
		content = StringUtils.removeCommentLines(content);
		content = StringUtils.removeNewlines(content);
		Dict dict = new Dict(content);
		assertEquals(70, dict.getAliasCount());
		assertEquals("WorkCity", dict.getValue("A2"));
		assertEquals("Custom2", dict.getValue("B7"));
		assertEquals("_AimScreenName", dict.getValue("A9"));
	}

	public void testDictSinglepartDict2() throws Exception {
		String content = StringUtils.fromResource("/singlepart.dict2.txt");
		content = StringUtils.removeCommentLines(content);
		content = StringUtils.removeNewlines(content);
		Dict dict = new Dict(content);
		assertEquals(150, dict.getAliasCount());
		assertEquals("25", dict.getValue("13D"));
		assertEquals("info@haller-systemservice.net", dict.getValue("8D"));
		assertEquals("Henry Chan", dict.getValue("C9"));
		assertEquals("'Gregor Lubina'", dict.getValue("139"));
	}

	public void testDereferenceEmptyDictException() throws Exception {
		try {
			Dict.dereference(null, Dict.EMPTY_LIST, null);
			fail("Exception expected");
		} catch (Exception expected) {
			assertEquals("Cannot dereference IDs without dictionaries",
					expected.getMessage());
		}
	}

	public void testDereferenceNoScopeDictsException() throws Exception {
		List<Dict> dicts = new LinkedList<Dict>();
		dicts.add(new Dict("<>"));
		try {
			Dict.dereference("", dicts, ScopeTypes.COLUMN_SCOPE);
			fail("Exception expected");
		} catch (Exception expected) {
			assertEquals("Dictionary could not dereference key:  in scope COLUMN_SCOPE",
					expected.getMessage());
		}
	}

	public void testDereferenceException() throws Exception {
		List<Dict> dicts = new LinkedList<Dict>();
		dicts.add(new Dict("<>"));
		try {
			Dict.dereference("", dicts, ScopeTypes.ATOM_SCOPE);
			fail("Exception expected");
		} catch (Exception expected) {
			assertEquals(
					"dereference() must be called with a reference id including the prefix '^'",
					expected.getMessage());
		}
	}

}
