package use_case.deleteEvent;

import use_case.addEvent.EventMethodsDataAccessInterface;

import java.util.UUID;

public class DeleteEventInteractor implements DeleteEventInputBoundary {

    private final EventMethodsDataAccessInterface repo;
    private final DeleteEventOutputBoundary presenter;

    public DeleteEventInteractor(EventMethodsDataAccessInterface repo,
                                 DeleteEventOutputBoundary presenter) {
        this.repo = repo;
        this.presenter = presenter;
    }

    @Override
    public void delete(DeleteEventInputData inputData) {
        UUID id = inputData.getId();

        if (!repo.existById(id)) {
            presenter.present(new DeleteEventOutputData(false, "Event not found"));
            return;
        }

        repo.delete(id);

        presenter.present(new DeleteEventOutputData(true, "Event deleted successfully"));
    }
}
