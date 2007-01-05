package mork;

import junit.framework.TestCase;

public class StringUtilsTest extends TestCase {

	public void testRemoveComments() throws Exception {
		assertEquals("test \n", StringUtils.removeCommentLines("test // Comment"));
		assertEquals("test \ntest2 \n", StringUtils
				.removeCommentLines("test // Comment\ntest2 // Comment 2\n"));
	}
	
	public void testRemoveNewlines() throws Exception {
		assertEquals("",StringUtils.removeNewlines("\n"));
	}

	public void testRemoveDoubleNewlines() throws Exception {
		assertEquals("\n",StringUtils.removeDoubleNewlines("\n\n"));
		assertEquals("\n",StringUtils.removeDoubleNewlines("\r\n"));
		assertEquals("\n",StringUtils.removeDoubleNewlines("\r\r"));
		assertEquals("\n",StringUtils.removeDoubleNewlines("\r\n\r\n"));
	}

}
