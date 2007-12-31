package mork;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class TableTest extends TestCase {

	public void testTableWithLiterals() throws Exception {
		String content = "{ 1:cards [ 1 (name=Jack) ] [ 2 (name=John)] }";
		Table table = new Table(content);
		assertEquals("1", table.getTableId());
		assertEquals("cards", table.getScopeName());
		assertEquals(2, table.getRows().size());

		Row row0 = table.getRows().get(0);
		assertEquals("1", row0.getRowId());
		assertEquals("Jack", row0.getValue("name"));

		Row row1 = table.getRows().get(1);
		assertEquals("2", row1.getRowId());
		assertEquals("John", row1.getValue("name"));
	}

	public void testTableWithReferencedScope() throws Exception {
		List<Dict> dicts = new LinkedList<Dict>();
		dicts.add(new Dict("<<(atomScope=c)>(80=cards)(81=name)>"));
		dicts.add(new Dict("<>"));
		String content = "{ 1:^80 [1 (^81=Foo)] }";
		Table table = new Table(content, dicts);
		assertEquals("1", table.getTableId());
		assertEquals("cards", table.getScopeName());
		assertEquals(1, table.getRows().size());

		Row row0 = table.getRows().get(0);
		assertEquals("1", row0.getRowId());
		assertEquals("Foo", row0.getValue("name"));

	}

	public void testTableWithoutScope() throws Exception {
		String content = "{ 1 [1 (foo=bar)] }";
		Table table = new Table(content);
		assertEquals("1", table.getTableId());
		assertNull(table.getScopeName());
		assertEquals(1, table.getRows().size());

		Row row0 = table.getRows().get(0);
		assertEquals("1", row0.getRowId());
		assertEquals("bar", row0.getValue("foo"));
	}

	public void testPattern() throws Exception {
		Pattern pattern2 = Pattern
				.compile("\\{\\s*([-\\w]*):\\^([0-9A-Z]*)\\s*\\{(.*)\\}\\s*-\\s*\\[-?([0-9A-F]*)\\]\\}");
		Matcher matcher2 = pattern2
				.matcher("{1:^80 {(k^BE:c)(s=9)} -  [-5F8]}");
		assertTrue(matcher2.matches());
		assertEquals("{1:^80 {(k^BE:c)(s=9)} -  [-5F8]}",matcher2.group());
		assertEquals("{1:^80 {(k^BE:c)(s=9)} -  [-5F8]}",matcher2.group(0));
		assertEquals("1",matcher2.group(1));
		assertEquals("80",matcher2.group(2));
		assertEquals("(k^BE:c)(s=9)",matcher2.group(3));
		assertEquals("5F8",matcher2.group(4));
	}

	public void testGroupRemoval() throws Exception {
		List<Dict> dicts = new LinkedList<Dict>();
		dicts.add(new Dict("<<(atomScope=c)>(80=cards)(81=name)>"));
		Table table = new Table("{1:^80 {(k^BE:c)(s=9)} -  [-5F8]}",dicts);
		assertEquals("1",table.getTableId());
		assertEquals("cards",table.getScopeName());
		assertEquals(1,table.getRows().size());
		Row row = table.getRows().get(0);
		assertEquals("5F8",row.getRowId());
	}
}
