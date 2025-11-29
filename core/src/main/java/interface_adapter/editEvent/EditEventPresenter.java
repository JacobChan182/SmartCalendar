package interface_adapter.editEvent;

import use_case.editEvents.EditEventOutputBoundary;
import use_case.editEvents.EditEventOutputData;

public class EditEventPresenter implements EditEventOutputBoundary {
    private final EditEventView view;

    public EditEventPresenter(EditEventView view) {
        this.view = view;
    }

    @Override
    public void present(EditEventOutputData outputData) {
        if (outputData.getSuccess()) {
            view.showEditedEvent(outputData.getUpdateEvent());
        } else {
            view.showError("Failed to update event");
        }

    }
}
