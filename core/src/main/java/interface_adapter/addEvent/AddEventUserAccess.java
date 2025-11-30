package interface_adapter.addEvent;

import use_case.addEvent.AddEventInputBoundary;
import use_case.addEvent.AddEventInputData;

import entity.Event;

public class AddEventUserAccess {
    private final AddEventInputBoundary interactor;

    public AddEventUserAccess(AddEventInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void addEvent(Event event) {
        AddEventInputData inputData = new AddEventInputData(
                event.getId(),
                event.getTitle(),
                event.getStart(),
                event.getEnd(),
                event.getLocation(),
                event.getCategory(),
                event.getReminderMessage()
        );
        interactor.addEvent(inputData);
    }
}
