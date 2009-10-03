package mork;

import java.io.IOException;
import java.io.InputStreamReader;
import junit.framework.TestCase;

/**
 * base class for JMork tests - supplies some utility functions
 * like getMorkDocument
 * @author wf
 *
 */
public abstract class JMorkTest extends TestCase {
	/**
	 * get the Mork Document with the given name
	 * @param fileName
	 * @return 
	 * @throws IOException 
	 */
	protected MorkDocument getMorkDocument(String fileName) throws IOException{
		InputStreamReader reader = new InputStreamReader(getClass()
				.getResource("/"+fileName).openStream());
		MorkDocument morkDocument = new MorkDocument(reader);
		return morkDocument;
	}
}
