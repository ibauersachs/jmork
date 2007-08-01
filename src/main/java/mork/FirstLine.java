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
    private static final String EXPRESSION = 
        "// <!-- <mdb:mork:z v=\\\"(\\d*)\\.(\\d*)\"/> -->";

    /** A real example of the line being parsed */
    private static final String SAMPLE = "// <!-- <mdb:mork:z v=\"1.4\"/> -->";

    /** Precompiled RegEx Pattern */
    private static final Pattern PATTERN = Pattern.compile(EXPRESSION);

    /** The primary version number, e.g. "1" */
    private String majorVersion;

    /** THe secondary version number, e.g. "4" */
    private String minorVersion;

    /** Concatenated version string, e.g. "1.4" */
    private String version;

    /**
     * Parse the Mork entry line which specifies the format and the version
     * information used by the Mork database file
     * 
     * @param value
     *            the first line of the Mork database file
     */
    public FirstLine(final String value) {
        Matcher matcher = PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new RuntimeException("Invalid Mork format: " + value + 
                                       ", should be: " + SAMPLE);
        }
        majorVersion = matcher.group(1);
        minorVersion = matcher.group(2);
        version = majorVersion + "." + minorVersion;
    }

    /**
     * Returns the Version identifier of the Mork Database file in the format
     * <code>X.Y</code> where X is the major version identifier and Y is the
     * minor version identifier
     * 
     * @return the version of the Mork file, e.g. <code>1.4</code>
     */
    public String getVersion() {
        return version;
    }

}
