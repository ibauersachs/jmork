package mork;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parses the first line of a Mork Database, which contains the identifier and a
 * version attribute in the format:
 * 
 * <pre>
 * // &lt;!-- &lt;mdb:mork:z v=&quot;1.4&quot;/&gt; --&gt;
 * </pre>
 * 
 * @author mhaller
 */
public class FirstLine {

	/** The format of the first line as regular expression */
	private static final String EXPRESSION = "// <!-- <mdb:mork:z v=\\\"(\\d*)\\.(\\d*)\"/> -->";

	/** A real example of the line being parsed */
	private static final String SAMPLE = "// <!-- <mdb:mork:z v=\"1.4\"/> -->";

	/** Precompiled RegEx Pattern */
	private static final Pattern PATTERN = Pattern.compile(EXPRESSION);

	private String majorVersion;

	private String minorVersion;

	private String version;

	public FirstLine(final String value) {
		Matcher matcher = PATTERN.matcher(value);
		if (!matcher.matches()) {
			throw new RuntimeException("Invalid Mork format: " + value
					+ ", should be: " + SAMPLE);
		}
		majorVersion = matcher.group(1);
		minorVersion = matcher.group(2);
		version = majorVersion + "." + minorVersion;
	}

	public String getVersion() {
		return version;
	}

}
