package mork;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities for parsing String content.
 * 
 * @author mhaller
 */
public class StringUtils {

	/**
	 * Removes C/C++-style single line comments from the given content
	 * 
	 * @param value
	 * @return
	 */
	public static String removeCommentLines(String value) {
		Pattern pattern = Pattern.compile("(//.*)([\\r\\n]|$)",
				Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(value);
		if (matcher.find()) {
			return matcher.replaceAll("\n");
		}
		return value;
	}

	/**
	 * Removes all newlines (Carriage Feed and Newline character) from the value
	 * 
	 * @param value
	 * @return
	 */
	public static String removeNewlines(String value) {
		return value.replaceAll("[\\n\\r]", "");
	}

	public static String removeDoubleNewlines(String value) {
		value = value.replaceAll("[\\n\\r]{2}", "\n");
		value = value.replaceAll("[\\n\\r]{2}", "\n");
		return value;
	}

	/**
	 * Loads a Classpath Resource and returns it as single String
	 * 
	 * @param resourceName
	 *            the classpath resource to load, relative to the StringUtils
	 *            class. E.g. use "/foo.txt" to load files from the root of the
	 *            classpath.
	 * @return a String with the whole content of the file. Newlines are
	 *         replaced with the newline character.
	 * @throws RuntimeException
	 *             if there was any IO error with the resource
	 */
	public static String fromResource(String resourceName) {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(
				StringUtils.class.getResourceAsStream(resourceName)));
		try {
			final StringBuffer buf = new StringBuffer();
			String line = reader.readLine();
			while (line != null) {
				buf.append(line);
				buf.append("\n");
				line = reader.readLine();
			}
			reader.close();
			return buf.toString();
		} catch (IOException e) {
			try {
				reader.close();
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			throw new RuntimeException(e);
		}
	}

}
