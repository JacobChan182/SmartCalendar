package interface_adapter.deleteEvents;

import use_case.deleteEvents.DeleteEventInputBoundary;
import use_case.deleteEvents.DeleteEventInputData;

public class DeleteEventController {

    private final DeleteEventInputBoundary interactor;

    public DeleteEventController(DeleteEventInputBoundary interactor) {
        this.interactor = interactor;
    }

    // called when click button. e.g. deleteButton.onClick -> controller.deleteEvent(id)
    public void deleteEvent(String eventId) {
        DeleteEventInputData inputData = new DeleteEventInputData(eventId);
        interactor.execute(inputData);
    }
}
