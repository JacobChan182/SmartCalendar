package use_case.deleteEvents;

public class DeleteEventInputData {
    private final String eventId;

    public DeleteEventInputData(String eventId) {
        this.eventId = eventId;
    }

    public String getEventId() {
        return eventId;
    }
}

