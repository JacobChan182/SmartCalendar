package use_case.deleteEvents;

import entity.CalendarEvent;
import java.util.List;

public class DeleteEventOutputData {
    private final boolean success;
    private final String message;
    private final List<CalendarEvent> remainingEvents;

    public DeleteEventOutputData(boolean success, String message, List<CalendarEvent> remainingEvents) {
        this.success = success;
        this.message = message;
        this.remainingEvents = remainingEvents;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<CalendarEvent> getRemainingEvents() {
        return remainingEvents;
    }
}
