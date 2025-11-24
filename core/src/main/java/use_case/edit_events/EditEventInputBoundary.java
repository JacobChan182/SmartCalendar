package use_case.edit_events;

public interface EditEventInputBoundary {
    /**
     * Executes the edit event use case.
     * @param editEventInputData the input data
     */
    void execute(EditEventInputData editEventInputData);
}
