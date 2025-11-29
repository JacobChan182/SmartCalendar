package interface_adapter.editEvent;

import java.util.UUID;
import java.time.LocalDateTime;

import entity.Event;
import use_case.editEvents.EditEventInputBoundary;
import use_case.editEvents.EditEventInputData;


public class EditEventUserAccess {
    private final EditEventInputBoundary interactor;

    public EditEventUserAccess(EditEventInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void editEvent(UUID id,
                          String title,
                          LocalDateTime start,
                          LocalDateTime end,
                          String location,
                          Event.CategoryType category,
                          String reminder) {
        EditEventInputData inputData = new EditEventInputData( id, title, start, end, location, category, reminder);
        interactor.editEvent(inputData);
    }

}
