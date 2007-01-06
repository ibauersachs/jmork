package mork;

import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

public class MorkDocumentTest extends TestCase {

	public void testSimpleMorkDocument() throws Exception {
		InputStreamReader reader = new InputStreamReader(getClass().getResource("/simple.mab").openStream());
		MorkDocument morkDocument = new MorkDocument(reader);

		List<Dict> dicts = morkDocument.getDicts();
		assertEquals(2,dicts.size());
		Dict dict0 = dicts.get(0);
		assertEquals(8,dict0.getAliasCount());
		assertSame(ScopeTypes.COLUMN_SCOPE,dict0.getDefaultScope());
		assertEquals("cards",dict0.getValue("80"));
		assertEquals("givenname",dict0.getValue("84"));
		
		Dict dict1 = dicts.get(1);
		assertEquals(7,dict1.getAliasCount());
		assertSame(ScopeTypes.ATOM_SCOPE,dict1.getDefaultScope());
		assertEquals("John",dict1.getValue("93"));
		
		List<Row> rows= morkDocument.getRows();
		assertEquals(1,rows.size());
		Row row = rows.get(0);
		assertEquals("cards",row.getScopeName());
		assertEquals("1",row.getRowId());
		assertEquals("John",row.getValue("givenname"));
	}
	
}
