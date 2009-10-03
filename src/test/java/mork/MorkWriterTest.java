package mork;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;

import junit.framework.TestCase;

/**
 * test the Mork Writer
 * @author wf
 *
 */
public class MorkWriterTest extends JMorkTest {
	MorkWriter writer;
	ByteArrayOutputStream ostream;
	MorkDocument morkDocument;
	private boolean debug=true;
	
	/**
	 * init this testcase with the given filename
	 * @param filename
	 */
	protected void init(String filename) throws Exception {
		morkDocument = super.getMorkDocument(filename);
		writer = new MorkWriter(morkDocument);
		ostream=new ByteArrayOutputStream();
	}
	
	/**
	 * set up the test case
	 */
	@Override
	protected void setUp() throws Exception {
		init("simple.mab");
	}
	
	/**
	 * test writing a Mork 1.4 header
	 */
	public void testHeader() throws Exception {
		writer.write(ostream);
		assertTrue(ostream.toString().startsWith(MorkWriter.zm_Magic));
	}
	
	/**
	 * test the dictionaries are being written
	 * @throws Exception
	 */
	public void testDict() throws Exception {
		writer.write(ostream);
		if (debug)
			System.out.println(ostream.toString());
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(ostream.toByteArray()));
		MorkDocument wDocument = new MorkDocument(reader);
		List<Dict> mDicts = morkDocument.getDicts();
		List<Dict> wDicts = wDocument.getDicts();
		// compare dicts gotten from written document to original ones
		assertEquals(mDicts.size(),wDicts.size());
		for (int i=0;i<mDicts.size();i++) {
			Dict mDict=mDicts.get(i);
			Dict wDict=wDicts.get(i);
			// check that there are as many aliases written as originally parsed
			assertEquals(wDict.getAliasCount(),mDict.getAliasCount());
			Aliases wAliases = wDict.getAliases();
			Aliases mAliases = mDict.getAliases();
			// check all values written to the ones originally parsed
			for (String id:mAliases.getKeySet()){
				String mValue=mAliases.getValue(id);
				String wValue=wAliases.getValue(id);
				assertEquals(mValue,wValue);
			}
		}	
	}
	
	/**
	 * test the tables and rows are being written
	 * @throws Exception
	 */
	public void testTables() throws Exception {
		init("panacea.dat");
		writer.write(ostream);
		if (debug)
			System.out.println(ostream.toString());
		InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(ostream.toByteArray()));
		MorkDocument wDocument = new MorkDocument(reader);
		List<Table> mTables = morkDocument.getTables();
		List<Table> wTables = wDocument.getTables();
	
		// compare tables gotten from written document to original ones
		assertEquals(mTables.size(),wTables.size());
		for (int i=0;i<mTables.size();i++) {
			Table mTable=mTables.get(i);
			Table wTable=wTables.get(i);
			// check that there are as many rows written as originally parsed
			List<Row> mRows = mTable.getRows();
			List<Row> wRows = wTable.getRows();
			assertEquals(mRows.size(),wRows.size());
			for (int j=0;j<mRows.size();j++) {
				Row mRow=mRows.get(i);
				Row wRow=wRows.get(i);
			// check all values written to the ones originally parsed
				for (String name:mRow.getKeySet()){
					String mValue=mRow.getValue(name);
					String wValue=wRow.getValue(name);
					assertEquals(mValue,wValue);
				}
			}
			
		}	
	}
	

}
