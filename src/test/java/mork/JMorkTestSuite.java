package mork;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *  test suite for the jMork Parser and writer
 */
public class JMorkTestSuite extends TestSuite {
	/**
	 * get the suite of all test cases 
	 * @return a suite with all test cases
	 */
	public static Test suite() {
		TestSuite result=new TestSuite();
		result.addTest(new TestSuite(AliasesTest.class));
		result.addTest(new TestSuite(BugJMORK1Test.class));
		result.addTest(new TestSuite(DictTest.class));
		result.addTest(new TestSuite(FirstLineTest.class));
		result.addTest(new TestSuite(GroupsTest.class));
		result.addTest(new TestSuite(LiteralParserTest.class));
		result.addTest(new TestSuite(MorkDocumentTest.class));
		result.addTest(new TestSuite(MorkParserTest.class));
		result.addTest(new TestSuite(RowTest.class));
		result.addTest(new TestSuite(StringUtilsTest.class));
		result.addTest(new TestSuite(TableTest.class));
		result.addTest(new TestSuite(MorkWriterTest.class));
		return result;
	}

}
