package use_case.edit_events;

public interface EditEventOutputBoundary {
    /**
     * Prepares the success view for the Edit Event Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(EditEventOutputData outputData);

    /**
     * Prepares the failure view for the Edit Event Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
