package use_case.addEvent;

import entity.Event;

import java.util.List;

/**
 * The Add Event Interactor
 */

public class AddEventInteractor implements AddEventInputBoundary{
    private final EventMethodsDataAccessInterface repo;
    private final AddEventOutputBoundary presenter;

    public AddEventInteractor(EventMethodsDataAccessInterface repo,
                              AddEventOutputBoundary presenter) {
        this.repo = repo;
        this.presenter = presenter;
    }


    @Override
    public void addEvent(AddEventInputData inputData) {
        if (inputData.getEnd().isBefore(inputData.getStart())) {
            presenter.present(new AddEventOutputData(
                    false,
                    null,
                    "End time cannot be before start time"));
            return;
        }

        Event event = new Event(
                inputData.getId(),
                inputData.getTitle(),
                inputData.getStart(),
                inputData.getEnd(),
                inputData.getLocation(),
                inputData.getCategory(),
                inputData.getReminder()
        );

        //Duplicates checking
        List<Event> eventsInDay = repo.getEventsForDay(event.getStart().toLocalDate());
        for (Event existing : eventsInDay) {
            boolean checkLocation = existing.getLocation().equalsIgnoreCase(event.getLocation());
            boolean checkTime = !(event.getEnd().isBefore(existing.getStart()) || event.getStart().isAfter(existing.getEnd()));
            if (checkLocation && checkTime) {
                presenter.present(new AddEventOutputData(false, event, "Conflict: overlapping events"));
                return;
            }
        }

        repo.save(event);
        AddEventOutputData addData = new AddEventOutputData(true, event, "Event added successfully");
        presenter.present(addData);
    }


}
