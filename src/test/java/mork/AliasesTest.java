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

}
