package interface_adapter.addEvent;

import entity.Event;

public interface AddEventView {
    void showAddedEvent(Event e);
    void showError(String message);
}
