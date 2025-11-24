package use_case.edit_events;

import java.time.LocalDateTime;
import java.util.UUID;

import entity.Event;
import entity.CategoryType;
import entity.Reminder;

public class EditEventInteractor implements EditEventInputBoundary {
    private final EditEventEventDataAccessInterface editEventEventDataAccessObject;
    private final EditEventOutputBoundary editEventPresenter;

    public EditEventInteractor(EditEventEventDataAccessInterface editEventEventDataAccessInterface,
                               EditEventOutputBoundary editEventPresenter) {
        this.editEventEventDataAccessObject = editEventEventDataAccessInterface;
        this.editEventPresenter = editEventPresenter;
    }

    @Override
    public void execute(EditEventInputData editEventInputData) {

        // Load existing event
        UUID id = editEventInputData.getId();
        Event event = editEventEventDataAccessObject.get(id);

        if (event == null) {
            editEventPresenter.prepareFailView("Event does not exist.");
            return;
        }


        // Assigning Title
        String newTitle;
        if (editEventInputData.getTitle() == null || editEventInputData.getTitle().isEmpty()) {
            newTitle = event.getTitle();
        } else {
            newTitle = editEventInputData.getTitle();
        }

        event.setTitle(newTitle);

        // Assigning start and end time
        LocalDateTime newStart;
        LocalDateTime newEnd;
        if (editEventInputData.getStart() == null) {
            newStart = event.getStart();
        } else if (editEventInputData.getStart() != null) {
            newStart = editEventInputData.getStart();
        }
        if (editEventInputData.getEnd() == null) {
            newEnd = event.getEnd();
        } else if (editEventInputData.getEnd() != null) {
            newEnd = editEventInputData.getEnd();
        }
        if (newEnd.isBefore(newStart)) {
            editEventPresenter.prepareFailView("End time cannot be before start time");
            return;
        }

        event.setStart(newStart);
        event.setEnd(newEnd);

        // Assigning location
        String newLocation;
        if (editEventInputData.getLocation() == null || editEventInputData.getLocation().isEmpty()) {
            newLocation = event.getLocation();
        } else if (editEventInputData.getLocation() != null) {
            newLocation = editEventInputData.getLocation();
        }

        event.set(newLocation);

        // Assigning category
        CategoryType newCategory;
        if (editEventInputData.getCategory() == null) {
            newCategory = event.getCategory();
        } else if (editEventInputData.getCategory() != null) {
            newCategory = editEventInputData.getCategory();
        }

        event.setCategory(newCategory);

        // Assigning reminder
        Reminder newReminder;
        if (editEventInputData.getReminder() == null) {
            newReminder = event.getReminder();
        } else if (editEventInputData.getReminder() != null) {
            newReminder = editEventInputData.getReminder();
        }

        event.setReminder(newReminder);


        eventDataAccess.save(event);

        EditEventOutputData editEventOutputData = new EditEventOutputData(
                event.getId(),
                event.getTitle(),
                event.getStart(),
                event.getEnd(),
                event.getLocation(),
                event.getCategory(),
                event.getReminder()
        );
        editEventPresenter.prepareSuccessView(editEventOutputData);
    }

}
