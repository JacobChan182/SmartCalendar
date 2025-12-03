
package use_case.deleteEvent;

import entity.Event;
import use_case.addEvent.EventMethodsDataAccessInterface;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DeleteEventInteractorTest {


    private static class InMemoryEventRepo implements EventMethodsDataAccessInterface {

        private final Map<UUID, Event> store = new HashMap<>();
        boolean deleteCalled = false;

        @Override
        public boolean existById(UUID id) {
            return store.containsKey(id);
        }

        @Override
        public void save(Event event) {
            store.put(event.getId(), event);
        }

        @Override
        public Event get(UUID id) {
            return store.get(id);
        }

        @Override
        public void delete(UUID id) {
            deleteCalled = true;
            store.remove(id);
        }

        @Override
        public List<Event> getEventsForDay(java.time.LocalDate date) {
            List<Event> result = new ArrayList<>();
            for (Event e : store.values()) {
                if (e.getStart().toLocalDate().equals(date)) {
                    result.add(e);
                }
            }
            return result;
        }
    }
    //delete event successfully
    @Test
    void successDeleteTest() {

        InMemoryEventRepo repo = new InMemoryEventRepo();

        UUID id = UUID.randomUUID();
        Event e = new Event(
                id,
                "Test Event",
                LocalDateTime.now(),
                LocalDateTime.now().plusHours(1),
                "Room A",
                Event.CategoryType.CASUAL,
                null
        );

        repo.save(e);

        DeleteEventOutputBoundary presenter = new DeleteEventOutputBoundary() {
            @Override
            public void present(DeleteEventOutputData data) {
                assertTrue(data.isSuccess());
                assertEquals("Event deleted successfully", data.getMessage());
                assertFalse(repo.existById(id));
            }
        };

        DeleteEventInteractor interactor = new DeleteEventInteractor(repo, presenter);

        DeleteEventInputData input = new DeleteEventInputData(id);

        interactor.delete(input);
        assertTrue(repo.deleteCalled);
    }

    // delete event does not exist
    @Test
    void failureEventNotFoundTest() {

        InMemoryEventRepo repo = new InMemoryEventRepo();

        UUID missingId = UUID.randomUUID();

        DeleteEventOutputBoundary presenter = new DeleteEventOutputBoundary() {
            @Override
            public void present(DeleteEventOutputData data) {
                assertFalse(data.isSuccess());
                assertEquals("Event not found", data.getMessage());
            }
        };

        DeleteEventInteractor interactor = new DeleteEventInteractor(repo, presenter);

        DeleteEventInputData input = new DeleteEventInputData(missingId);

        interactor.delete(input);
        assertFalse(repo.deleteCalled);
    }
}
