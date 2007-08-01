package mork;

/**
 * The Event Listener Interfaces is notified of Parser Events when the Mork
 * Parser parses Mork elements like Dictionaries, Tables, Rows etc.
 * 
 * @author mhaller
 */
public interface EventListener {

    /**
     * Is being called for all toplevel elements when parsing a Mork database.
     * 
     * The event has a type (one of {@link EventType} and an optional content.
     * The content is a String value and usually contains the whole Mork content
     * of the parses element.
     * 
     * In cases where it does not make sense to have content, the value is
     * simply <code>null</code>
     * 
     * Note: the Mork Parser will reuse a single Event object for all events,
     * resetting its values to new values. If you need to reuse the Event
     * object, you need to make sure to clone it yourself.
     * 
     * @param event
     *            the Event, which is of type {@link EventType} and an optional
     *            String value which might be <code>null</code>. The event
     *            object itself is never <code>null</code>.
     */
    void onEvent(Event event);

}
