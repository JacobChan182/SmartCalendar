package use_case.deleteEvents;

import java.util.List;
import entity.CalendarEvent;

public interface EventDataAccessInterface {

    boolean existsById(String eventId);

    CalendarEvent getById(String eventId);

    void deleteById(String eventId);

    List<CalendarEvent> getAllEvents();
}
