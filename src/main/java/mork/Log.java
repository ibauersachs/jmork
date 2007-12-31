package mork;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * A simple logging class
 * 
 * @author mhaller
 */
class Log {

	private String sourceClazz;

	public Log(Object source) {
		if (source == null) {
			sourceClazz = Object.class.getName();
		} else {
			sourceClazz = source.getClass().getName();
		}
	}

	public String getSourceClassname() {
		return sourceClazz;
	}

	/**
	 * Logs a warning-level message and returns the full message
	 * 
	 * @param message
	 *            the human-readable message text to print
	 * @param throwable
	 *            an optional exception
	 * @return
	 */
	public String warn(String message, Throwable throwable) {
		StringWriter writer = new StringWriter();
		String output = String.format("WARN %tT [%s]: %s", new Date(),
				sourceClazz, message);
		writer.append(output);
		if (throwable != null) {
			writer.append("\n");
			throwable.printStackTrace(new PrintWriter(writer));
		}
		String content = writer.toString();
		System.err.println(content);
		return content;
	}

}
