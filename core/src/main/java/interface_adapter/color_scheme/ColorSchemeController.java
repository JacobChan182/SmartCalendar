package interface_adapter.color_scheme;

import use_case.get_color_scheme.GetColorSchemeInputBoundary;
import use_case.get_color_scheme.GetColorSchemeInputData;

/**
 * The controller for the Get Color Scheme Use Case.
 */
public class ColorSchemeController {

    private final GetColorSchemeInputBoundary getColorSchemeUseCaseInteractor;

    public ColorSchemeController(GetColorSchemeInputBoundary getColorSchemeUseCaseInteractor) {
        this.getColorSchemeUseCaseInteractor = getColorSchemeUseCaseInteractor;
    }

    /**
     * Executes the Get Color Scheme Use Case.
     * @param hexColor the hexadecimal color code (with or without #)
     */
    public void execute(String hexColor) {
        GetColorSchemeInputData inputData = new GetColorSchemeInputData(hexColor);
        getColorSchemeUseCaseInteractor.execute(inputData);
    }
}

