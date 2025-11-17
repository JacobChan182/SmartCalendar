package use_case.edit_events;

import java.time.LocalDateTime;
import java.util.UUID;

public class EditEventInteractor implements EditEventInputBoundary {
    private final EditEventEventDataAccessInterface editEventEventDataAccessObject;
    private final EditEventOutputData editEventPresenter;

    public EditEventInteractor(EditEventEventDataAccessInterface editEventEventDataAccessInterface,
                               EditEventOutputBoundary editEventOutputBoundary) {
        this.editEventEventDataAccessObject = editEventEventDataAccessInterface;
        this.editEventPresenter = editEventOutputBoundary;
    }

    @Override
    public void execute(EditEventInputData editEventInputData) {

        // Load existing event
        Event event = eventDataAccessObject.get(editEventInputData.getId());

        // Assigning Title
        if (editEventInputData.getTitle() == null || editEventInputData.getTitle().isEmpty()) {
            String newTitle = event.getTitle();
        } else {
            String newTitle = editEventInputData.getTitle();
        }

        event.setTitle(newTitle);

        // Assigning start and end time
        if (editEventInputData.getStart() == null) {
            LocalDateTime newStart = event.getStart();
        } else if (editEventInputData.getEnd() == null) {
            LocalDateTime newEnd = event.getEnd();
        } else if (editEventInputData.getStart() != null) {
            LocalDateTime newStart = editEventInputData.getStart();
        } else if (editEventInputData.getEnd() != null) {
            LocalDateTime newEnd = editEventInputData.getEnd();
        }

        event.setStart(newStart);
        event.setEnd(newEnd);

        // Assigning location
        if (editEventInputData.getLocation() == null || editEventInputData.getLocation().isEmpty()) {
            String newLocation = event.getLocation();
        } else if (editEventInputData.getLocation() != null) {
            String newLocation = editEventInputData.getLocation();
        }

        // Assigning category
        if (editEventInputData.getCategory() == null) {
            CategoryType newCategory = event.getCategory();
        } else if (editEventInputData.getCategory() != null) {
            CategoryType newCategory = editEventInputData.getCategory();
        }

        event.setCategory(newCategory);

        // Assigning reminder
        if (editEventInputData.getReminder() == null) {
            Reminder newReminder = event.getReminder();
        } else if (editEventInputData.getReminder() != null) {
            Reminder newReminder = editEventInputData.getReminder();
        }

    }

}
