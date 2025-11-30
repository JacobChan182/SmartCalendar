package use_case.edit_events;

import entity.Event;
import entity.Event.CategoryType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Interactor for the Edit Event use case.
 *
 * It DOES NOT mutate the existing Event object (no setters on Event).
 * Instead, it creates a new Event with updated fields and saves it.
 */
public class EditEventInteractor implements EditEventInputBoundary {

    private final EditEventEventDataAccessInterface eventDataAccess;
    private final EditEventOutputBoundary presenter;

    public EditEventInteractor(EditEventEventDataAccessInterface eventDataAccess,
                               EditEventOutputBoundary presenter) {
        this.eventDataAccess = eventDataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(EditEventInputData inputData) {

        // ===== 1. Load existing event =====
        UUID id = inputData.getId();
        Event event = eventDataAccess.get(id);

        if (event == null) {
            presenter.prepareFailView("Event does not exist.");
            return;
        }

        // ===== 2. Title =====
        String newTitle = inputData.getTitle();
        if (newTitle == null || newTitle.isBlank()) {
            newTitle = event.getTitle();   // keep old title if none provided
        }

        // ===== 3. Start & End time =====
        LocalDateTime newStart = inputData.getStart();
        LocalDateTime newEnd   = inputData.getEnd();

        if (newStart == null) {
            newStart = event.getStart();
        }
        if (newEnd == null) {
            newEnd = event.getEnd();
        }

        // basic validation: end must not be before start
        if (newEnd.isBefore(newStart)) {
            presenter.prepareFailView("End time cannot be before start time.");
            return;
        }

        // ===== 4. Location =====
        String newLocation = inputData.getLocation();
        if (newLocation == null || newLocation.isBlank()) {
            newLocation = event.getLocation();
        }

        // ===== 5. Category =====
        CategoryType newCategory = inputData.getCategory();
        if (newCategory == null) {
            newCategory = event.getCategory();
        }

        // ===== 6. Reminder (unchanged) =====
        // 我们不编辑 reminder，只保留原来的值
        String reminderMessage = event.getReminderMessage();

        // ===== 7. Create updated Event (no setters on Event) =====
        Event updatedEvent = new Event(
                event.getId(),          // keep same id
                newTitle,
                newStart,
                newEnd,
                newLocation,
                newCategory,
                reminderMessage
        );

        // Save via data access
        eventDataAccess.save(updatedEvent);

        // ===== 8. Prepare output and call presenter =====
        EditEventOutputData output = new EditEventOutputData(
                updatedEvent.getId(),
                updatedEvent.getTitle(),
                updatedEvent.getStart(),
                updatedEvent.getEnd(),
                updatedEvent.getLocation(),
                updatedEvent.getCategory(),
                updatedEvent.getReminderMessage()   // 只是回传，不支持修改
        );

        presenter.prepareSuccessView(output);
    }
}
