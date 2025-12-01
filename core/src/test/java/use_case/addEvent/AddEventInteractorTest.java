package use_case.addEvent;

import entity.Event;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class AddEventInteractorTest {

    @Nested
    class TestRepo implements EventMethodsDataAccessInterface {
        private final Map<UUID, Event> events = new HashMap<>();

        @Override
        public void save(Event event) {events.put(event.getId(), event);}

        @Override
        public boolean existById(UUID id) {return events.containsKey(id);}

        @Override
        public void delete(UUID id) {events.remove(id);}

        @Override
        public Event get(UUID id) {return events.get(id);}

        @Override
        public List<Event> getEventsForDay(LocalDate date) {
            List<Event> result = new ArrayList<>();
            for (Event event: events.values()) {
                if (event.getStart().toLocalDate().equals(date)) {
                    result.add(event);
                }
            }
            return result;
        }
    static class TestPresenter implements AddEventOutputBoundary {
            AddEventOutputData lastOutput;

            @Override
            public void present(AddEventOutputData outputData) {
                this.lastOutput = outputData;
            }
    }

    @Test
    void testAddEventSuccess() {
            TestRepo testRepo = new TestRepo();
            TestPresenter testPresenter = new TestPresenter();
            AddEventInteractor interactor = new AddEventInteractor(testRepo, testPresenter);

            Event event = new Event(UUID.randomUUID(),
                    "Meeting",
                    LocalDateTime.of(2025, 11, 28, 10, 0),
                    LocalDateTime.of(2025, 11, 28, 11,0),
                    "Office",
                    Event.CategoryType.WORK,
                    "Meeting at work");

            interactor.addEvent(new AddEventInputData(
                    event.getId(),
                    event.getTitle(),
                    event.getStart(),
                    event.getEnd(),
                    event.getLocation(),
                    event.getCategory(),
                    event.getReminderMessage()
            ));

            assertTrue(testPresenter.lastOutput.getSuccess());
            assertEquals("Meeting",
                    testPresenter.lastOutput.getEvent().getTitle());
            assertEquals(1, testRepo.getEventsForDay(event.getStart().toLocalDate()).size());
        }

        @Test
        void testAddEventConflict() {
            TestRepo testRepo = new TestRepo();
            TestPresenter testPresenter = new TestPresenter();
            AddEventInteractor interactor = new AddEventInteractor(testRepo, testPresenter);

            Event existing = new Event(UUID.randomUUID(),
                "Lock in session",
                    LocalDateTime.of(2025, 12, 2, 10,0),
                    LocalDateTime.of(2025, 12, 3, 2, 0),
                    "Robarts Common",
                    Event.CategoryType.MEETING,
                    "Lock in expeditiously");
            testRepo.save(existing);

            Event duplicate = new Event(UUID.randomUUID(),
                    "Lock in session",
                    LocalDateTime.of(2025, 12, 2, 10,30),
                    LocalDateTime.of(2025, 12, 3, 2, 30),
                    "Robarts Common",
                    Event.CategoryType.MEETING,
                    "Lock in expeditiously");
            interactor.addEvent(new AddEventInputData(duplicate.getId(),
                    duplicate.getTitle(),
                    duplicate.getStart(),
                    duplicate.getEnd(),
                    duplicate.getLocation(),
                    duplicate.getCategory(),
                    duplicate.getReminderMessage()));
            assertFalse(testPresenter.lastOutput.getSuccess());
            assertEquals("Conflict: overlapping events", testPresenter.lastOutput.getMessage());
            assertEquals(1, testRepo.getEventsForDay(existing.getStart().toLocalDate()).size());

        }

        @Test
        void TestAddTwoEvents() {
            TestPresenter testPresenter = new TestPresenter();
            TestRepo testRepo = new TestRepo();
            AddEventInteractor interactor = new AddEventInteractor(testRepo, testPresenter);

            Event event1 = new Event(
                    UUID.randomUUID(),
                    "Exam 1",
                    LocalDateTime.of(2025,12,10,9,0),
                    LocalDateTime.of(2025,12,10,12,0),
                    "EX100",
                    Event.CategoryType.MEETING,
                    "Exam for CS258");

            interactor.addEvent(new AddEventInputData(event1.getId(),
                    event1.getTitle(),
                    event1.getStart(),
                    event1.getEnd(),
                    event1.getLocation(),
                    event1.getCategory(),
                    event1.getReminderMessage()));

            Event event2 = new Event(
                    UUID.randomUUID(),
                    "Exam 2",
                    LocalDateTime.of(2025,12,10,17,0),
                    LocalDateTime.of(2025,12,10,20,0),
                    "EX200",
                    Event.CategoryType.MEETING,
                    "Exam for CSC207");

            interactor.addEvent(new AddEventInputData(event2.getId(),
                    event2.getTitle(),
                    event2.getStart(),
                    event2.getEnd(),
                    event2.getLocation(),
                    event2.getCategory(),
                    event2.getReminderMessage()));

            List<Event> events = testRepo.getEventsForDay(LocalDate.of(2025, 12, 10));
            assertEquals(2, events.size());

            boolean foundEvent1 = false;
            boolean foundEvent2 = false;
            for (Event event: events ) {
                if (event.getTitle().equals("Exam 1")) {
                    foundEvent1 = true;
                }
                if (event.getTitle().equals("Exam 2")) {
                    foundEvent2 = true;
                }
            }
            assertTrue(foundEvent1);
            assertTrue(foundEvent2);
        }



        @Test
        void TestAddEndBeforeStart() {
            TestPresenter testPresenter = new TestPresenter();
            TestRepo testRepo = new TestRepo();
            AddEventInteractor interactor = new AddEventInteractor(testRepo, testPresenter);


            Event event = new Event(
                    UUID.randomUUID(),
                    "Exam 1",
                    LocalDateTime.of(2025,12,10,9,0),
                    LocalDateTime.of(2025,12,9,12,0),
                    "EX100",
                    Event.CategoryType.MEETING,
                    "I'm eepy");

            interactor.addEvent(new AddEventInputData(event.getId(),
                    event.getTitle(),
                    event.getStart(),
                    event.getEnd(),
                    event.getLocation(),
                    event.getCategory(),
                    event.getReminderMessage()));

            assertFalse(testPresenter.lastOutput.getSuccess());
            assertEquals("End time cannot be before start time", testPresenter.lastOutput.getMessage());
        }

        @Test
        void TestSameLocationTimeOverlap() {
            TestRepo testRepo = new TestRepo();
            TestPresenter testPresenter = new TestPresenter();
            AddEventInteractor interactor = new AddEventInteractor(testRepo, testPresenter);

            Event existing = new Event(
                    UUID.randomUUID(),
                    "Meeting 1",
                    LocalDateTime.of(2025, 12, 10, 10, 0),
                    LocalDateTime.of(2025, 12, 10, 11, 0),
                    "Office",
                    Event.CategoryType.WORK,
                    null
            );

            testRepo.save(existing);

            Event newEvent = new Event(
                    UUID.randomUUID(),
                    "Meeting 2",
                    LocalDateTime.of(2025, 12, 10, 10, 30),
                    LocalDateTime.of(2025, 12, 10, 11, 30),
                    "Office",
                    Event.CategoryType.WORK,
                    null
            );

            interactor.addEvent(new AddEventInputData(
                    newEvent.getId(),
                    newEvent.getTitle(),
                    newEvent.getStart(),
                    newEvent.getEnd(),
                    newEvent.getLocation(),
                    newEvent.getCategory(),
                    newEvent.getReminderMessage()
            ));

            assertFalse(testPresenter.lastOutput.getSuccess());
            assertEquals("Conflict: overlapping events", testPresenter.lastOutput.getMessage());
        }

        @Test
        void testEventEndsBeforeExistingStarts() {
            TestRepo testRepo = new TestRepo();
            TestPresenter testPresenter = new TestPresenter();
            AddEventInteractor interactor = new AddEventInteractor(testRepo, testPresenter);

            Event existing = new Event(UUID.randomUUID(),
                    "Meeting 1",
                    LocalDateTime.of(2025, 12, 10, 14, 0),
                    LocalDateTime.of(2025, 12, 10, 15, 0),
                    "Office",
                    Event.CategoryType.WORK,
                    null);
            testRepo.save(existing);

            Event newEvent = new Event(UUID.randomUUID(),
                    "Meeting 2",
                    LocalDateTime.of(2025, 12, 10, 10, 0),
                    LocalDateTime.of(2025, 12, 10, 11, 0),  // Ends before existing starts
                    "Office",
                    Event.CategoryType.WORK,
                    null);

            interactor.addEvent(new AddEventInputData(
                    newEvent.getId(),
                    newEvent.getTitle(),
                    newEvent.getStart(),
                    newEvent.getEnd(),
                    newEvent.getLocation(),
                    newEvent.getCategory(),
                    newEvent.getReminderMessage()));

            assertTrue(testPresenter.lastOutput.getSuccess());
            assertEquals(2, testRepo.getEventsForDay(LocalDate.of(2025, 12, 10)).size());
        }

        @Test
        void testEventStartsAfterExistingEnds() {

            TestRepo testRepo = new TestRepo();
            TestPresenter testPresenter = new TestPresenter();
            AddEventInteractor interactor = new AddEventInteractor(testRepo, testPresenter);

            Event existing = new Event(UUID.randomUUID(),
                    "Meeting 1",
                    LocalDateTime.of(2025, 12, 10, 10, 0),
                    LocalDateTime.of(2025, 12, 10, 11, 0),
                    "Office",
                    Event.CategoryType.WORK,
                    null);
            testRepo.save(existing);

            Event newEvent = new Event(UUID.randomUUID(),
                    "Meeting 2",
                    LocalDateTime.of(2025, 12, 10, 12, 0),
                    LocalDateTime.of(2025, 12, 10, 13, 0),
                    "Office",
                    Event.CategoryType.WORK,
                    null);

            interactor.addEvent(new AddEventInputData(
                    newEvent.getId(),
                    newEvent.getTitle(),
                    newEvent.getStart(),
                    newEvent.getEnd(),
                    newEvent.getLocation(),
                    newEvent.getCategory(),
                    newEvent.getReminderMessage()));

            assertTrue(testPresenter.lastOutput.getSuccess());
            assertEquals(2, testRepo.getEventsForDay(LocalDate.of(2025, 12, 10)).size());
        }
    }
}
