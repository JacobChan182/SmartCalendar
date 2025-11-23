package interface_adapter.deleteEvents;

import java.util.List;
import entity.CalendarEvent;

public class DeleteEventViewModel {

    private boolean lastOperationSuccess;
    private String message;
    private List<CalendarEvent> remainingEvents;

    public boolean isLastOperationSuccess() {
        return lastOperationSuccess;
    }

    public void setLastOperationSuccess(boolean lastOperationSuccess) {
        this.lastOperationSuccess = lastOperationSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<CalendarEvent> getRemainingEvents() {
        return remainingEvents;
    }

    public void setRemainingEvents(List<CalendarEvent> remainingEvents) {
        this.remainingEvents = remainingEvents;
    }

    // UI needed!
    public void printToConsole() {
        System.out.println("Success: " + lastOperationSuccess);
        System.out.println("Message: " + message);
        System.out.println("Remaining events:");
        if (remainingEvents != null) {
            for (CalendarEvent e : remainingEvents) {
                System.out.println(
                        " - [" + e.getId() + "] "
                                + e.getTitle()
                                + " at " + e.getStartTime()
                );
            }
        }
    }
}
