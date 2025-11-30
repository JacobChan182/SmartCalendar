package use_case.edit_events;

import java.time.LocalDateTime;
import java.util.UUID;
import entity.Event;
import use_case.addEvent.EventMethodsDataAccessInterface;

public class EditEventInteractor implements EditEventInputBoundary {
    private final EventMethodsDataAccessInterface eventMethodsDataAccess;
    private final EditEventOutputBoundary editEventPresenter;

    public EditEventInteractor(EventMethodsDataAccessInterface eventMethodsDataAccess,
                               EditEventOutputBoundary editEventPresenter) {
        this.eventMethodsDataAccess = eventMethodsDataAccess;
        this.editEventPresenter = editEventPresenter;
    }

    @Override
    public void execute(EditEventInputData editEventInputData) {

        // Load existing event
        UUID id = editEventInputData.getId();

        if (!eventMethodsDataAccess.existById(id)) {
            editEventPresenter.prepareFailView("Event does not exist.");
            return;
        }

        Event event = eventMethodsDataAccess.get(id);
        // Assigning Title
        String newTitle;
        if (editEventInputData.getTitle() == null || editEventInputData.getTitle().isEmpty()) {
            newTitle = event.getTitle();
        } else {
            newTitle = editEventInputData.getTitle();
        }


        // Assigning start and end time
        LocalDateTime newStart = editEventInputData.getStart();
        if (newStart == null) {
            newStart = event.getStart();
        }

        LocalDateTime newEnd = editEventInputData.getEnd();
        if (newEnd == null) {
            newEnd = event.getEnd();
        }

        if (newEnd.isBefore(newStart)) {
            editEventPresenter.prepareFailView("End time cannot be before start time");
            return;
        }


        // Assigning location
        String newLocation = editEventInputData.getLocation();
        if (newLocation == null || newLocation.isEmpty()) {
            newLocation = event.getLocation();
        }

        // Assigning category
        Event.CategoryType newCategory = editEventInputData.getCategory();
        if (newCategory == null) {
            newCategory = event.getCategory();
        }


        // Assigning reminder
        String newReminder = editEventInputData.getReminder();
        if (newReminder == null) {
            newReminder = event.getReminderMessage();
        }

        // Make a new event with updated information
        Event updated = new Event(
                event.getId(),
                newTitle,
                newStart,
                newEnd,
                newLocation,
                newCategory,
                newReminder
        );

        // Save updated event via DAO
        eventMethodsDataAccess.save(updated);

        // build output data from updatedEvent
        EditEventOutputData outputData = new EditEventOutputData(
                updated.getId(),
                updated.getTitle(),
                updated.getStart(),
                updated.getEnd(),
                updated.getLocation(),
                updated.getCategory(),
                updated.getReminderMessage()
        );

        editEventPresenter.prepareSuccessView(outputData);
    }

}
