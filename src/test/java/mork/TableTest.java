package mork;

import java.util.LinkedList;
import java.util.List;

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

}
