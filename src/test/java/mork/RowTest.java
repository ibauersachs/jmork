package mork;

import java.util.Arrays;

import junit.framework.TestCase;

public class RowTest extends TestCase {

	public void testInvalid() throws Exception {
		try {
			new Row("test");
			fail("Exception expected");
		} catch (Exception expected) {
			assertEquals("Row does not match RegEx: test", expected
					.getMessage());
		}
	}

	public void testSimpleRow() throws Exception {
		String content = "[ 1 (name=Jack) ]";
		Row row = new Row(content);
		assertEquals("1", row.getRowId());
		assertEquals("Jack", row.getValue("name"));
	}

	public void testSingleRow() throws Exception {
		String content = "[ 1:cards (dn=cn=John Hackworth,mail=jhackworth@atlantis.com)(modifytimestamp=19981001014531Z)(cn=John Hackworth)(givenname=John)(mail=jhackworth@atlantis.com)(xmozillausehtmlmail=FALSE)(sn=Hackworth)]";
		Row row = new Row(content);
		assertEquals("1", row.getRowId());
		assertEquals("cards", row.getScopeName());
		assertEquals("cn=John Hackworth,mail=jhackworth@atlantis.com", row
				.getValue("dn"));
		assertEquals("jhackworth@atlantis.com", row.getValue("mail"));
		assertEquals("19981001014531Z", row.getValue("modifytimestamp"));
		assertEquals("John Hackworth", row.getValue("cn"));
		assertEquals("John", row.getValue("givenname"));
		assertEquals("FALSE", row.getValue("xmozillausehtmlmail"));
		assertEquals("Hackworth", row.getValue("sn"));
	}

	public void testRowWithOIDs() throws Exception {
		String columnDictString = "< <(atomScope=c)> (80=cards)(81=dn)(82=modifytimestamp)(83=cn) (84=givenname)(85=mail)(86=xmozillausehtmlmail)(87=sn)>";
		Dict columnDict = new Dict(columnDictString);

		String atomDictString = "<(90=cn=John Hackworth,mail=jhackworth@atlantis.com)(91=19981001014531Z)(92=John Hackworth)(93=John)(94=jhackworth@atlantis.com)(95=FALSE)(96=Hackworth)>";
		Dict atomDict = new Dict(atomDictString);

		String content = "[1:^80(^81^90)(^82^91)(^83^92)(^84^93)(^85^94)(^86^95)(^87^96)]";
		Row row = new Row(content, Arrays.asList(new Dict[] { columnDict,
				atomDict }));

		assertEquals("1", row.getRowId());
		assertEquals("cards", row.getScopeName()); // 80
		assertEquals("cn=John Hackworth,mail=jhackworth@atlantis.com", row
				.getValue("dn")); // 81 - 90
		assertEquals("19981001014531Z", row.getValue("modifytimestamp")); // 82 -
		// 91
		assertEquals("John Hackworth", row.getValue("cn")); // 83 - 92
		assertEquals("John", row.getValue("givenname")); // 84 - 93
		assertEquals("jhackworth@atlantis.com", row.getValue("mail")); // 85 -
		// 94
		assertEquals("FALSE", row.getValue("xmozillausehtmlmail")); // 86 - 95
		assertEquals("Hackworth", row.getValue("sn")); // 87 - 96
	}

}
