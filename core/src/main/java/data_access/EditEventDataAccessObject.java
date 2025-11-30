package data_access;

import entity.Event;
import use_case.edit_events.EditEventEventDataAccessInterface;

import java.util.Map;
import java.util.UUID;

public class EditEventDataAccessObject implements EditEventEventDataAccessInterface {

    private final Map<UUID, Event> events;

    public EditEventDataAccessObject(Map<UUID, Event> events) {
        this.events = events;
    }

    @Override
    public Event get(UUID id) {
        return events.get(id);
    }

    @Override
    public void save(Event event) {
        events.put(event.getId(), event);
    }

    @Override
    public boolean exists(UUID id) {
        return events.containsKey(id);
    }
}