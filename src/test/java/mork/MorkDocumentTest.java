package mork;

import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class MorkDocumentTest extends TestCase {

	private boolean debug;


	public void testSimpleMorkDocument() throws Exception {
		InputStreamReader reader = new InputStreamReader(getClass()
				.getResource("/simple.mab").openStream());
		MorkDocument morkDocument = new MorkDocument(reader);

		List<Dict> dicts = morkDocument.getDicts();
		assertEquals(2, dicts.size());
		Dict dict0 = dicts.get(0);
		assertEquals(8, dict0.getAliasCount());
		assertSame(ScopeTypes.COLUMN_SCOPE, dict0.getDefaultScope());
		assertEquals("cards", dict0.getValue("80"));
		assertEquals("givenname", dict0.getValue("84"));

		Dict dict1 = dicts.get(1);
		assertEquals(7, dict1.getAliasCount());
		assertSame(ScopeTypes.ATOM_SCOPE, dict1.getDefaultScope());
		assertEquals("John", dict1.getValue("93"));

		List<Row> rows = morkDocument.getRows();
		assertEquals(1, rows.size());
		Row row = rows.get(0);
		assertEquals("cards", row.getScopeName());
		assertEquals("1", row.getRowId());
		assertEquals("John", row.getValue("givenname"));
	}
	

	/**
	 * test reading a panacea.dat file
	 * @throws Exception
	 */
	public void testPanaceaDocument() throws Exception {
		InputStreamReader reader = new InputStreamReader(getClass()
				.getResource("/panacea.dat").openStream());
		MorkDocument morkDocument = new MorkDocument(reader);
		List<Dict> dicts = morkDocument.getDicts();
		assertEquals(2, dicts.size());
		Dict dict0 = dicts.get(0);
		assertEquals(26, dict0.getAliasCount());
		assertSame(ScopeTypes.COLUMN_SCOPE, dict0.getDefaultScope());
		assertEquals("ns:msg:db:row:scope:folders:all", dict0.getValue("80"));
		assertEquals("totalMsgs", dict0.getValue("84"));
		assertEquals("folderName", dict0.getValue("8B"));
		assertEquals("serverRecent", dict0.getValue("93"));

		Dict dict1 = dicts.get(1);
		assertEquals(65, dict1.getAliasCount());
		assertSame(ScopeTypes.ATOM_SCOPE, dict1.getDefaultScope());
		assertEquals("Entwuerfe", dict1.getValue("93"));
		
		List<Table> tables = morkDocument.getTables();
		assertEquals(1, tables.size());
		Table table = tables.get(0);
		List<Row> rows = table.getRows();
		assertEquals(17, rows.size());
		for (int i=0;i<rows.size();i++){
			Row row = rows.get(i);
			//assertEquals("cards", row.getScopeName());
			// assertEquals(""+(i+1), row.getRowId());
			Map<String, Alias> map = row.getAliases();
			//assertEquals(10,map.size());
			for (String key:map.keySet()) {
				Alias alias=map.get(key);
				String value=alias.getValue();
				if (debug)
					System.out.println(""+i+":"+key+"="+value);
			}
			String charSet=row.getValue("charset");
			if (charSet!=null && !charSet.equals("")){
				assertEquals("ISO-8859-15", charSet);
			}
		}
	
		
	}

}
