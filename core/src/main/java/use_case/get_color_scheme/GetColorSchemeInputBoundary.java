package use_case.get_color_scheme;

/**
 * Input Boundary for the Get Color Scheme Use Case.
 */
public interface GetColorSchemeInputBoundary {

    /**
     * Executes the get color scheme use case.
     * @param inputData the input data containing the hex color
     */
    void execute(GetColorSchemeInputData inputData);
}

