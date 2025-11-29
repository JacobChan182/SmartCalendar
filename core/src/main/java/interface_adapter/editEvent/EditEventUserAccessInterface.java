package interface_adapter.editEvent;

import java.time.LocalDateTime;
import java.util.UUID;

import entity.Event;

public interface EditEventUserAccessInterface {
    void editEvent(UUID id,
                   String title,
                   LocalDateTime start,
                   LocalDateTime end,
                   String location,
                   Event.CategoryType category);

    void addEvent(Event event);
}
