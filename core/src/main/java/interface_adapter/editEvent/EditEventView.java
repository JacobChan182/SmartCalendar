package interface_adapter.editEvent;

import entity.Event;

public interface EditEventView {
    void showEditedEvent(Event event);
    void showError(String message);
}
