package data_access;

import entity.Event;
import use_case.addEvent.EventMethodsDataAccessInterface;

import java.time.LocalDate;
import java.util.*;

public class InMemoryEventDataAccessObject implements EventMethodsDataAccessInterface {
    private final Map<UUID, Event> events = new HashMap<>();

    @Override
    public void save(Event event) {
        events.put(event.getId(), event);
    }

    @Override
    public boolean existById(UUID id) {
        return events.containsKey(id);
    }

    @Override
    public void delete(UUID id) {
        events.remove(id);
    }

    @Override
    public List<Event> getEventsForDay(LocalDate date) {
        List<Event> result = new ArrayList<>();
        for (Event e : events.values()) {
            if (e.getStart().toLocalDate().equals(date)) {
                result.add(e);
            }
        }
        return result;
    }

    @Override
    public Event get(UUID id) {
        return events.get(id);
    }
}
