package mork;

import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

public class AliasesTest extends TestCase {

	public void testAliasesStringDictDict1() {
		Dict dict = new Dict("<(80=bar)>");
		Aliases aliases = new Aliases("(foo^80)", Collections
				.singletonList(dict));
		assertEquals("bar", aliases.getValue("foo"));
	}

	public void testAliasesStringDictDict3() {
		Dict dict = new Dict("<(80=foo)(81=bar)>");
		Aliases aliases = new Aliases("(foo^81)", Collections
				.singletonList(dict));
		assertEquals("bar", aliases.getValue("foo"));
	}

	public void testAliasesStringDictDict4() {
		Dict dict1 = new Dict("<<a=c>(80=foo)>");
		Dict dict2 = new Dict("<(81=bar)>");
		Aliases aliases = new Aliases("(^80=bar)", Arrays.asList(new Dict[] {
				dict1, dict2 }));
		assertEquals("bar", aliases.getValue("foo"));
	}

	public void testCommentURLProblem() throws Exception {
		String content = "(B7=b)(81=Mike)(82=Haller)(83=)(84=Mike Haller)(85=mhaller)(86\n    =mike.haller@smartwerkz.com)(87=info@mhaller.de)(88=1)(80=0)(89\n    =Aspenweg 16)(8A=Eriskirch)(8B=BW)(8C=88097)(8D=Deutschland)(8E\n    =http://www.smartwerkz.com/)(8F=Nico)";
		Aliases aliases = new Aliases(content);
		assertEquals("Nico", aliases.getValue("8F"));
	}

	/**
	 * Test values with escaped character.
	 * 
	 * JIRA Issue: JMORK-1
	 * 
	 * @throws Exception
	 */
	public void testEscaping() throws Exception {
		Aliases aliases = new Aliases("(foo=(bar\\))");
		assertEquals("(bar)", aliases.getValue("foo"));
	}

	public void testInvalidAliasTooShort() throws Exception {
		try {
			new Aliases("()");
			fail("RuntimeException expected");
		} catch (RuntimeException expected) {
			assertEquals("Alias must be at least 3 characters: ()",expected.getMessage());
		}
	}
	
}
