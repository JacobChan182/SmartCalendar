package entities;



public class Reminder {
    private final String eventId;
    private final String message;

    public Reminder(String eventId, String message) {
        this.eventId = eventId;
        this.message = message;
    }

    public String getEventId() { return eventId; }
    public String getMessage() { return message; }
}

