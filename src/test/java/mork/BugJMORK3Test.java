package mork;

import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class BugJMORK3Test extends TestCase {

	public void testJMORK3() throws Exception {
		InputStreamReader reader = new InputStreamReader(getClass()
				.getResource("/abook_JMORK-3.mab").openStream());
		new MorkDocument(reader);
	}
	
	public void testTable() throws Exception {
		List<Dict> dicts = new LinkedList<Dict>();
		dicts.add(new Dict("<<(atomScope=c)>(80=cards)(81=name)(83=foo)(BA=baz)>"));
		dicts.add(new Dict("<>(FD9=bar)(1116=bat)>"));
		
		StringBuilder sb = new StringBuilder();
		sb.append("{2:^80 {(k^C4:c)(s=9)} -\n");
		sb.append("  [-43F]-\n");
		sb.append("  [-441]-\n");
		sb.append("  [-443]-\n");
		sb.append("  [-445]-\n");
		sb.append("  [-447]-\n");
		sb.append("  [-449]-\n");
		sb.append("  [-44B]-\n");
		sb.append("  [-44D]-\n");
		sb.append("  [-44F]-\n");
		sb.append("  [-451]-\n");
		sb.append("  [-453]-\n");
		sb.append("  [-455]-\n");
		sb.append("  [-457]-\n");
		sb.append("  [-470]\n");
		sb.append("  [-64E(^83^FD9)(^BA^1116)]}\n");
		new Table(sb.toString(),dicts);
	}

}
