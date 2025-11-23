package use_case.deleteEvents;

public class DeleteEventInteractor implements DeleteEventInputBoundary {

    private final EventDataAccessInterface eventDAO;
    private final DeleteEventOutputBoundary presenter;

    public DeleteEventInteractor(EventDataAccessInterface eventDAO,
                                 DeleteEventOutputBoundary presenter) {
        this.eventDAO = eventDAO;
        this.presenter = presenter;
    }

    @Override
    public void execute(DeleteEventInputData inputData) {
        String eventId = inputData.getEventId();

        if (eventId == null || eventId.isEmpty()) {
            DeleteEventOutputData outputData = new DeleteEventOutputData(
                    false,
                    "Event ID cannot be empty.",
                    eventDAO.getAllEvents()
            );
            presenter.present(outputData);
            return;
        }

        if (!eventDAO.existsById(eventId)) {
            DeleteEventOutputData outputData = new DeleteEventOutputData(
                    false,
                    "Event not found: " + eventId,
                    eventDAO.getAllEvents()
            );
            presenter.present(outputData);
            return;
        }

        // delete successfully
        eventDAO.deleteById(eventId);

        DeleteEventOutputData outputData = new DeleteEventOutputData(
                true,
                "Event deleted: " + eventId,
                eventDAO.getAllEvents()
        );
        presenter.present(outputData);
    }
}
