package entities;


import java.time.LocalDateTime;

public class Reminder {
    private final String eventId;
    private final LocalDateTime remindAt;
    private final String message;

    public Reminder(String eventId, LocalDateTime remindAt, String message) {
        this.eventId = eventId;
        this.remindAt = remindAt;
        this.message = message;
    }

    public String getEventId() { return eventId; }
    public LocalDateTime getRemindAt() { return remindAt; }
    public String getMessage() { return message; }
}

