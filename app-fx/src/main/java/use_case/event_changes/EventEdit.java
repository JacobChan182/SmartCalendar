package use_case.event_changes;

import entities.Event;

import java.util.*;

import java.time.LocalDate;

public class EventEdit {
    //Store events in a mapping
    private final Map<UUID, Event> events = new HashMap<>();

    public void addEvent(Event event) {
        events.put(event.getId(), event);
    }

    public void addEvents(List<Event> eventList) {
        for (Event e : eventList) {
            addEvent(e);
        }
    }

    public boolean editEvent(UUID eventId, Event updatedEvent) {
        if (events.containsKey(eventId)) {
            events.put(eventId, updatedEvent);
            return true;
        }
        return false;
    }

    public boolean deleteEvent(UUID eventId) {
        return events.remove(eventId) != null;
    }

    public List<Event> getAllEvents() {
        return new ArrayList<>(events.values());
    }

    public boolean editEvent(Event event) {
        UUID id = event.getId();
        if (events.containsKey(id)) {
            events.put(id, event);
            return true;
        }
        return false;
    }

    public boolean updatedEvent(Event event) {
        return editEvent(event.getId(), event);
    }

    public List<Event> getEventsForDay(LocalDate date) {
        List<Event> result = new ArrayList<>();
        for (Event e : events.values()) {
            if (e.getStart().toLocalDate().equals(date)) {
                result.add(e);
            }
        }
        return result;
    }
}
