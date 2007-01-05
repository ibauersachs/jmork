package mork;

import junit.framework.TestCase;

public class GroupsTest extends TestCase {

	public void testGroups() throws Exception {
		String content = StringUtils.fromResource("/groups.txt");
		Groups groups = new Groups(content);
		assertEquals(2, groups.countGroups());

		Group group0 = groups.getGroup(0);
		Group group1 = groups.getGroup(1);

		assertEquals("4F", group0.getTransactionId());
		assertEquals("< (foo=bar) >{ 1 	[ 1 (foo=bar1) ]}[ 1 (foo=bar2) ]",
				group0.getContent());
		assertEquals("50", group1.getTransactionId());
		assertEquals("[ 2 (foo=bar3) ]", group1.getContent());
	}

}
