package use_case.editEvents;

import entity.Event;

public class EditEventOutputData {
    private final boolean success;
    private final Event updateEvent;

    public EditEventOutputData(boolean success, Event updateEvent) {
        this.success = success;
        this.updateEvent = updateEvent;
    }
    public boolean getSuccess() {return success;}
    public Event getUpdateEvent() {return updateEvent;}

}
