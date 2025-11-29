package use_case.editEvents;

import entity.Event;
import use_case.addEvent.EventMethodsDataAccessInterface;

public class EditEventInteractor implements EditEventInputBoundary {
    private final EventMethodsDataAccessInterface eventRep;
    private final EditEventOutputBoundary presenter;

    public EditEventInteractor(EventMethodsDataAccessInterface eventRep, EditEventOutputBoundary presenter) {
        this.eventRep = eventRep;
        this.presenter = presenter;
    }

    @Override
    public void editEvent(EditEventInputData inputBoundary) {
        Event updated = new Event(
                inputBoundary.getId(),
                inputBoundary.getTitle(),
                inputBoundary.getStart(),
                inputBoundary.getEnd(),
                inputBoundary.getLocation(),
                inputBoundary.getCategory(),
                inputBoundary.getReminderMessage());

        boolean success = eventRep.existById(updated.getId());
        if (success) {
            eventRep.save(updated);
        }
        EditEventOutputData outputData = new EditEventOutputData(success, updated);
        presenter.present(outputData);
    }
}

