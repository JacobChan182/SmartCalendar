package use_case.edit_events;

import entity.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EditEventInteractorTest {
    private static class InMemoryEditDataAccessObject implements EditEventEventDataAccessInterface {
        private final Map<UUID, Event> events = new HashMap<>();

        @Override
        public Event get(UUID id) {
            return events.get(id);
        }

        @Override
        public void save(Event event) {
            events.put(event.getId(), event);
        }

        @Override
        public boolean exists(UUID id) {
            return events.containsKey(id);
        }
    }

    private static class CapturingPresenter implements EditEventOutputBoundary {
        EditEventOutputData successData;
        String failMessage;

        @Override
        public void prepareSuccessView(EditEventOutputData outputData) {
            this.successData = outputData;
        }

        @Override
        public void prepareFailView(String error) {
            this.failMessage = error;
        }
    }

    // Update the title, location, and the category
    @Test
    void successTest() {
        UUID id = UUID.randomUUID();
        LocalDateTime originalStart = LocalDateTime.now();
        LocalDateTime originalEnd = originalStart.plusHours(1);

        Event originalEvent = new Event(
                id, "Original Title", originalStart, originalEnd,
                "Toronto", Event.CategoryType.WORK, "Don't forget"
        );

        InMemoryEditDataAccessObject inMemoryEditDataAccessObject = new InMemoryEditDataAccessObject();
        inMemoryEditDataAccessObject.save(originalEvent);

        EditEventInputData inputData = new EditEventInputData(
                id, "Updated Title", originalStart, originalEnd,
                "Mississauga", Event.CategoryType.GYM, null
        );

        EditEventOutputBoundary successPresenter = new EditEventOutputBoundary() {
            @Override
            public void prepareSuccessView(EditEventOutputData outputData) {
                assertEquals("Updated Title", outputData.getTitle());
                assertEquals("Mississauga", outputData.getLocation());
                assertEquals(Event.CategoryType.GYM, outputData.getCategory());
                assertEquals(id, outputData.getId());
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected");
            }
        };
        EditEventInteractor editEventInteractor = new EditEventInteractor(inMemoryEditDataAccessObject, successPresenter);

        editEventInteractor.execute(inputData);

        Event storedEvent = inMemoryEditDataAccessObject.get(id);
        assertEquals("Updated Title", storedEvent.getTitle());
        assertEquals("Mississauga", storedEvent.getLocation());
    }

    // Update the title
    @Test
    void successPartialUpdateTest() {
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2025,12,1,10,0);
        LocalDateTime end = LocalDateTime.of(2025,12,1,10,30);

        Event originalEvent = new Event(
                id, "Original Title", start, end,
                "Toronto", Event.CategoryType.CASUAL, "Reminder"
        );
        InMemoryEditDataAccessObject inMemoryEditDataAccessObject = new InMemoryEditDataAccessObject();
        inMemoryEditDataAccessObject.save(originalEvent);

        EditEventInputData inputData = new EditEventInputData(
                id, "Updated Title", null, null, null, null, null
        );

        EditEventOutputBoundary successPresenter = new EditEventOutputBoundary() {
            @Override
            public void prepareSuccessView(EditEventOutputData outputData) {
                assertEquals("Updated Title", outputData.getTitle());
                assertEquals("Toronto", outputData.getLocation());
                assertEquals(start, outputData.getStart());
                assertEquals(end, outputData.getEnd());
                assertEquals(Event.CategoryType.CASUAL, outputData.getCategory());
                assertEquals("Reminder", outputData.getReminderMessage());
            }
            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected");
            }
        };

        EditEventInteractor editEventInteractor = new EditEventInteractor(inMemoryEditDataAccessObject, successPresenter);
        editEventInteractor.execute(inputData);
    }

    // Update location and reminder keeping the title
    @Test
    void successNoTitleChangeTest() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        Event originalEvent = new Event(
                id, "Keep This Title", start, end,
                "Toronto", Event.CategoryType.CASUAL, "Reminder");

        InMemoryEditDataAccessObject inMemoryEditDataAccessObject = new InMemoryEditDataAccessObject();
        inMemoryEditDataAccessObject.save(originalEvent);

        EditEventInputData inputData = new EditEventInputData(
                id, null, null, null, "Mississauga", null, "Reminder2"
        );

        CapturingPresenter presenter = new CapturingPresenter();
        EditEventInteractor interactor = new EditEventInteractor(inMemoryEditDataAccessObject, presenter);

        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.successData);
        assertEquals("Keep This Title", presenter.successData.getTitle(), "Title should remain unchanged when input is null");
        assertEquals("Mississauga", presenter.successData.getLocation(), "Location should be updated");
        assertEquals("Reminder2", presenter.successData.getReminderMessage(), "Reminder should be updated");
    }

    // The event id does not exist
    @Test
    void failureEventNotFoundTest() {
        InMemoryEditDataAccessObject inMemoryEditDataAccessObject = new InMemoryEditDataAccessObject();

        EditEventInputData inputData = new EditEventInputData(
                UUID.randomUUID(), "Title", null, null, null, null, null
        );

        CapturingPresenter presenter = new CapturingPresenter();
        EditEventInteractor interactor = new EditEventInteractor(inMemoryEditDataAccessObject, presenter);

        interactor.execute(inputData);

        assertNull(presenter.successData);
        assertEquals("Event does not exist.", presenter.failMessage);
    }

    // Changes the start time to after the end time
    @Test
    void failureEndTimeBeforeStartTimeTest() {
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2025,12,1,10,0);
        LocalDateTime end = LocalDateTime.of(2025,12,1,10,30);

        Event originalEvent = new Event(
                id, "Title", start, end, "Toronto",  Event.CategoryType.WORK, "Don't forget"
        );

        InMemoryEditDataAccessObject inMemoryEditDataAccessObject = new InMemoryEditDataAccessObject();
        inMemoryEditDataAccessObject.save(originalEvent);

        LocalDateTime newStart = LocalDateTime.of(2025,12,1,11,0);

        EditEventInputData inputData = new EditEventInputData(
                id, null, newStart, null, null, null, null
        );

        CapturingPresenter presenter = new CapturingPresenter();
        EditEventInteractor editEventInteractor = new EditEventInteractor(inMemoryEditDataAccessObject, presenter);

        editEventInteractor.execute(inputData);

        if (presenter.successData != null) {
            fail("Expected failure but got success.\n" +
                    "Updated Start: " + presenter.successData.getStart() + "\n" +
                    "Updated End:   " + presenter.successData.getEnd());
        }

        assertNotNull(presenter.failMessage, "Expected a failure message");
        assertEquals("End time cannot be before start time.", presenter.failMessage);
    }

    // Changes the end time to before the start time
    @Test
    void failureEndTimeBeforeStartTimeTest2() {
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.of(2025,12,1,10,0);
        LocalDateTime end = LocalDateTime.of(2025,12,1,10,30);

        Event originalEvent = new Event(
                id, "Title", start, end, "Toronto",  Event.CategoryType.WORK, "Don't forget"
        );

        InMemoryEditDataAccessObject inMemoryEditDataAccessObject = new InMemoryEditDataAccessObject();
        inMemoryEditDataAccessObject.save(originalEvent);

        LocalDateTime newEnd = LocalDateTime.of(2025,12,1,9,0);

        EditEventInputData inputData = new EditEventInputData(
                id, null, null, newEnd, null, null, null
        );

        CapturingPresenter presenter = new CapturingPresenter();
        EditEventInteractor editEventInteractor = new EditEventInteractor(inMemoryEditDataAccessObject, presenter);

        editEventInteractor.execute(inputData);

        if (presenter.successData != null) {
            fail("Expected failure but got success.\n" +
                    "Updated Start: " + presenter.successData.getStart() + "\n" +
                    "Updated End:   " + presenter.successData.getEnd());
        }

        assertNotNull(presenter.failMessage, "Expected a failure message");
        assertEquals("End time cannot be before start time.", presenter.failMessage);
    }

    // Test empty strings ("") which should be treated like null
    @Test
    void successEmptyStringsTest() {
        // Arrange
        UUID id = UUID.randomUUID();
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        Event originalEvent = new Event(
                id, "Keep This Title", start, end,
                "Keep This Location", Event.CategoryType.CASUAL, "Reminder");

        InMemoryEditDataAccessObject repository = new InMemoryEditDataAccessObject();
        repository.save(originalEvent);

        // Act: Pass empty strings "" for title and location
        EditEventInputData inputData = new EditEventInputData(
                id, "", null, null, "   ",null, null
        );

        CapturingPresenter presenter = new CapturingPresenter();
        EditEventInteractor interactor = new EditEventInteractor(repository, presenter);

        interactor.execute(inputData);

        // Assert
        assertNotNull(presenter.successData);
        // Should keep original title because input was blank
        assertEquals("Keep This Title", presenter.successData.getTitle());
        // Should keep original location because input was blank
        assertEquals("Keep This Location", presenter.successData.getLocation());
    }
}
