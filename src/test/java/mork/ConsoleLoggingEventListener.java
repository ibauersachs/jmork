package mork;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class ConsoleLoggingEventListener implements EventListener {

	private static final Logger LOG = Logger.getLogger("mork");

	{
		try {
			InputStream config = getClass().getResourceAsStream(
					"/logging.properties");
			if (config != null) {
				LogManager.getLogManager().readConfiguration(config);
			}
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static final String MSG_FINE = "Event Type {0}";

	private static final String MSG_FINEST = "Event Type {0}: Value={1}";

	public void onEvent(Event event) {
		if (LOG.getLevel() == Level.FINE) {
			LOG.log(Level.FINE, MSG_FINE, new Object[] { event.eventType });
		} else if (LOG.getLevel() == Level.FINEST) {
			LOG.log(Level.FINE, MSG_FINEST, new Object[] { event.eventType,
					event.value });
		}
	}

}
