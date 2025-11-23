package interface_adapter.deleteEvents;

import use_case.deleteEvents.DeleteEventOutputBoundary;
import use_case.deleteEvents.DeleteEventOutputData;

public class DeleteEventPresenter implements DeleteEventOutputBoundary {

    private final DeleteEventViewModel viewModel;

    public DeleteEventPresenter(DeleteEventViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(DeleteEventOutputData outputData) {
        viewModel.setLastOperationSuccess(outputData.isSuccess());
        viewModel.setMessage(outputData.getMessage());
        viewModel.setRemainingEvents(outputData.getRemainingEvents());

        // UI needed!
        viewModel.printToConsole();
    }
}

