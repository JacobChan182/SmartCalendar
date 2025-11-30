package use_case.addEvent;

/**
 * Input Boundary for actions that involve adding events
 */

public interface AddEventInputBoundary {
    /**
     * Adding a new event to out list
     * @param inputData is the input data
     */
    void addEvent(AddEventInputData inputData);
}
