package mork;

import java.util.LinkedList;
import java.util.List;

public class MockEventListener implements EventListener {

	private List<Event> events = new LinkedList<Event>();

	public void onEvent(Event event) {
		Event clone = new Event();
		clone.eventType = event.eventType;
		clone.value = event.value;
		events.add(clone);
	}

	public Event pop() {
		if (events.isEmpty()) {
			return null;
		}
		return events.remove(0);
	}

}
