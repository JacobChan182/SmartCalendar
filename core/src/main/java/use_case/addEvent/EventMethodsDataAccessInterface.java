package use_case.addEvent;

import java.util.UUID;
import java.util.List;

import entity.Event;

public interface EventMethodsDataAccessInterface {
    /**
     * Checks if the given event does exist or not by UUID
     * @param id the ID to look for
     * @return true if the event with the ID exists; false if it does not exist
     */
    boolean existById(UUID id);

    /**
     * Save the event
     * @param event is the event to save
     */
    void save(Event event);

    /**
     * Return the event with the given ID
     * @param id is the event to look for
     * @return the event with the given id
     */
    Event get(UUID id);

    /**
     * Delete the event with the given ID
     * @param id is the event to delete
     */
    void delete(UUID id);
    List<Event> getEventsForDay(java.time.LocalDate date);
}