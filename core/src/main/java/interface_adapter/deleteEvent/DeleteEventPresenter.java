package interface_adapter.deleteEvent;

import use_case.deleteEvent.DeleteEventOutputBoundary;
import use_case.deleteEvent.DeleteEventOutputData;

public class DeleteEventPresenter implements DeleteEventOutputBoundary {

    private final DeleteEventView view;

    public DeleteEventPresenter(DeleteEventView view) {
        this.view = view;
    }

    @Override
    public void present(DeleteEventOutputData outputData) {
        if (outputData.isSuccess()) {
            view.showDeleteSuccess(outputData.getMessage());
        } else {
            view.showDeleteFailure(outputData.getMessage());
        }
    }
}
