package use_case.deleteEvent;

import java.util.UUID;

public class DeleteEventInputData {
    private final UUID id;

    public DeleteEventInputData(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}
