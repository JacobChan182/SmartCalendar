package use_case.addEvent;

/**
 * The output boundary for the Add Event Use Case
 */
public interface AddEventOutputBoundary {
    /**
     * Prepares the presenter for the Add Event Use Case
     * @param outputData is the output data
     */
    void present(AddEventOutputData outputData);
}
