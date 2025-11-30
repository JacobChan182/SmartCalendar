package interface_adapter.deleteEvent;

import use_case.deleteEvent.DeleteEventInputBoundary;
import use_case.deleteEvent.DeleteEventInputData;

import java.util.UUID;

public class DeleteEventController {

    private final DeleteEventInputBoundary interactor;

    public DeleteEventController(DeleteEventInputBoundary interactor) {
        this.interactor = interactor;
    }

    public void deleteEvent(UUID id) {
        DeleteEventInputData data = new DeleteEventInputData(id);
        interactor.delete(data);
    }
}
