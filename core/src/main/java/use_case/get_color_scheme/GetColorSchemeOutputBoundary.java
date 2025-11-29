package use_case.get_color_scheme;

/**
 * Output Boundary for the Get Color Scheme Use Case.
 */
public interface GetColorSchemeOutputBoundary {
    /**
     * Prepares the success view with color schemes.
     * @param outputData the output data containing all color schemes
     */
    void prepareSuccessView(GetColorSchemeOutputData outputData);

    /**
     * Prepares the failure view with an error message.
     * @param errorMessage the error message
     */
    void prepareFailView(String errorMessage);
}

