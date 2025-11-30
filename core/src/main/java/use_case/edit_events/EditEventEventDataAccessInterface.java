package use_case.edit_events;

import java.util.UUID;
import entity.Event;

public interface EditEventEventDataAccessInterface {
    Event get(UUID id);
    void save(Event event);
    boolean exists(UUID id);
}