package mozilla.thunderbird;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *  test suite for the jMork Parser ThunderBird support classes
 */
public class ThunderBirdTestSuite extends TestSuite {
	/**
	 * get the suite of all test cases 
	 * @return a suite with all test cases
	 */
	public static Test suite() {
		TestSuite result=new TestSuite();
		result.addTest(new TestSuite(AddressBookTest.class));
		result.addTest(new TestSuite(AddressComparatorTest.class));
		result.addTest(new TestSuite(AddressAccessTest.class));
		return result;
	}

}
