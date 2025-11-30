package use_case.deleteEvent;

public class DeleteEventOutputData {
    private final boolean success;
    private final String message;

    public DeleteEventOutputData(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
}
