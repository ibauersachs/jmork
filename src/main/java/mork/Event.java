package mork;

import java.io.Serializable;

/**
 * For each element the MorkParser has recognized, an Event is fired. This class
 * represents such an Event.
 * 
 * Please pay attention that the Events fired by the MorkParser are reused. If
 * you need to store Event data, copy the Event and store it yourself.
 * 
 * @author mhaller
 */
public class Event implements Serializable {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = -953791935775686254L;

	/**
	 * The type of the Event, one of {@link EventType}, must never be null
	 */
	public EventType eventType;

	/**
	 * A literal value associated with the event. Some events (of the
	 * end-of-element events) contain values, others use <code>null</code>
	 * here if no additional data is available.
	 */
	public String value;
}
