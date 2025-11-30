package use_case.addEvent;

import entity.Event;

/**
 * Output Data for the Add Event Use Case
 */

public class AddEventOutputData {
    private final boolean success;
    private final Event event;
    private final String message;

    public AddEventOutputData(boolean success, Event event, String message) {
        this.success = success;
        this.event = event;
        this.message = message;
    }

    public boolean getSuccess() {return success;}
    public Event getEvent() {return event;}
    public String getMessage() {return message;}
}
