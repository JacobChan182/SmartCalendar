package use_case.edit_events;

import entity.Event;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for EditEventInteractor.
 * Goal: cover all branches in EditEventInteractor to achieve 100% line coverage.
 */
class EditEventInteractorTest {

    /**
     * Simple DAO stub for tests.
     * It does not talk to the real database; it only stores one Event in memory.
     */
    private static class EditEventDaoStub implements EditEventEventDataAccessInterface {

        Event storedEvent;

        EditEventDaoStub(Event initialEvent) {
            this.storedEvent = initialEvent;
        }

        @Override
        public Event get(UUID id) {
            if (storedEvent != null && storedEvent.getId().equals(id)) {
                return storedEvent;
            }
            return null;
        }

        @Override
        public void save(Event event) {
            this.storedEvent = event;
        }

        @Override
        public boolean exists(UUID id) {
            return storedEvent != null && storedEvent.getId().equals(id);
        }
    }

    /**
     * Presenter stub for tests.
     * It only records how prepareSuccessView / prepareFailView are called.
     */
    private static class EditEventPresenterStub implements EditEventOutputBoundary {

        EditEventOutputData lastResponse;
        String lastError;
        boolean successCalled = false;
        boolean failCalled = false;

        @Override
        public void prepareSuccessView(EditEventOutputData outputData) {
            successCalled = true;
            lastResponse = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            failCalled = true;
            lastError = errorMessage;
        }
    }

    @Test
    void successWhenEventExistsAndTimesAreValid() {
        // Arrange
        UUID id = UUID.randomUUID();

        Event original = new Event(
                id,
                "Old title",
                LocalDateTime.of(2025, 12, 1, 9, 0),
                LocalDateTime.of(2025, 12, 1, 10, 0),
                "Old location",
                Event.CategoryType.BUSINESS,
                "Old reminder"
        );

        EditEventDaoStub dao = new EditEventDaoStub(original);
        EditEventPresenterStub presenter = new EditEventPresenterStub();

        EditEventInteractor interactor = new EditEventInteractor(dao, presenter);

        // In this test we pass null for most fields so that the "keep original value"
        // branches are exercised, but the edit still succeeds.
        EditEventInputData input = new EditEventInputData(
                id,
                null,                            // keep old title
                null,                            // keep old start
                null,                            // keep old end
                null,                            // keep old location
                null,                            // keep old category
                null                             // keep old reminder
        );

        // Act
        interactor.execute(input);

        // Assert
        assertTrue(presenter.successCalled);
        assertFalse(presenter.failCalled);
        assertNotNull(presenter.lastResponse);

        // The stored event should still exist and keep all original fields.
        assertEquals("Old title", dao.storedEvent.getTitle());
        assertEquals("Old location", dao.storedEvent.getLocation());
        assertEquals(Event.CategoryType.BUSINESS, dao.storedEvent.getCategory());
        assertEquals("Old reminder", dao.storedEvent.getReminderMessage());
    }

    @Test
    void failWhenEventDoesNotExist() {
        // Arrange
        UUID id = UUID.randomUUID();

        // DAO has no event stored: get(id) will return null.
        EditEventDaoStub dao = new EditEventDaoStub(null);
        EditEventPresenterStub presenter = new EditEventPresenterStub();
        EditEventInteractor interactor = new EditEventInteractor(dao, presenter);

        EditEventInputData input = new EditEventInputData(
                id,
                "Whatever",
                LocalDateTime.of(2025, 12, 1, 9, 0),
                LocalDateTime.of(2025, 12, 1, 10, 0),
                "Somewhere",
                Event.CategoryType.BUSINESS,
                "Reminder"
        );

        // Act
        interactor.execute(input);

        // Assert
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        // This message comes directly from your EditEventInteractor
        assertEquals("Event does not exist.", presenter.lastError);
        assertNull(dao.storedEvent);
    }

    @Test
    void failWhenEndTimeBeforeStartTime() {
        // Arrange
        UUID id = UUID.randomUUID();

        Event original = new Event(
                id,
                "Original",
                LocalDateTime.of(2025, 12, 1, 9, 0),
                LocalDateTime.of(2025, 12, 1, 10, 0),
                "Original location",
                Event.CategoryType.BUSINESS,
                "Original reminder"
        );

        EditEventDaoStub dao = new EditEventDaoStub(original);
        EditEventPresenterStub presenter = new EditEventPresenterStub();
        EditEventInteractor interactor = new EditEventInteractor(dao, presenter);

        // End time is before start time: should hit the validation failure branch.
        EditEventInputData input = new EditEventInputData(
                id,
                "New title",
                LocalDateTime.of(2025, 12, 1, 11, 0),
                LocalDateTime.of(2025, 12, 1, 10, 0),   // end < start
                "New location",
                Event.CategoryType.GYM,
                "New reminder"
        );

        // Act
        interactor.execute(input);

        // Assert
        assertFalse(presenter.successCalled);
        assertTrue(presenter.failCalled);
        assertEquals("End time cannot be before start time.", presenter.lastError);

        // The original event should not be modified when the edit fails.
        assertEquals("Original", dao.storedEvent.getTitle());
        assertEquals("Original location", dao.storedEvent.getLocation());
        assertEquals(Event.CategoryType.BUSINESS, dao.storedEvent.getCategory());
        assertEquals("Original reminder", dao.storedEvent.getReminderMessage());
    }
}
