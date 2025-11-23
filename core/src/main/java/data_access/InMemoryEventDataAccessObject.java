package data_access;

import java.time.LocalDateTime;
import java.util.*;
import entity.CalendarEvent;
import use_case.deleteEvents.EventDataAccessInterface;

public class InMemoryEventDataAccessObject implements EventDataAccessInterface {

    private final Map<String, CalendarEvent> events = new HashMap<>();

    public InMemoryEventDataAccessObject() {
        // fake data for testing!
        // e1: CS Lecture
        CalendarEvent e1 = new CalendarEvent(
                "e1",
                "CS Lecture",
                LocalDateTime.of(2025, 11, 24, 10, 0),
                "Data Structures lecture"
        );
        // e2: Doctor Appointment
        CalendarEvent e2 = new CalendarEvent(
                "e2",
                "Doctor Appointment",
                LocalDateTime.of(2025, 11, 25, 15, 30),
                "Annual health check"
        );
        // e3: Team Meeting
        CalendarEvent e3 = new CalendarEvent(
                "e3",
                "Team Meeting",
                LocalDateTime.of(2025, 11, 26, 9, 0),
                "Project discussion"
        );

        events.put(e1.getId(), e1);
        events.put(e2.getId(), e2);
        events.put(e3.getId(), e3);
    }

    @Override
    public boolean existsById(String eventId) {
        return events.containsKey(eventId);
    }

    @Override
    public CalendarEvent getById(String eventId) {
        return events.get(eventId);
    }

    @Override
    public void deleteById(String eventId) {
        events.remove(eventId);
    }

    @Override
    public List<CalendarEvent> getAllEvents() {
        return new ArrayList<>(events.values());
    }
}

