package use_case.get_color_scheme;

import java.util.List;

/**
 * The Get Color Scheme Interactor.
 */
public class GetColorSchemeInteractor implements GetColorSchemeInputBoundary {
    private final GetColorSchemeUserDataAccessInterface colorApiDataAccessObject;
    private final GetColorSchemeOutputBoundary colorSchemePresenter;

    public GetColorSchemeInteractor(GetColorSchemeUserDataAccessInterface colorApiDataAccessObject,
                                    GetColorSchemeOutputBoundary colorSchemePresenter) {
        this.colorApiDataAccessObject = colorApiDataAccessObject;
        this.colorSchemePresenter = colorSchemePresenter;
    }

    @Override
    public void execute(GetColorSchemeInputData inputData) {
        String hexColor = inputData.getHexColor();
        
        // Remove # if present
        hexColor = hexColor.replace("#", "").trim();
        
        // Validate hex color format
        if (!hexColor.matches("^[0-9A-Fa-f]{6}$")) {
            colorSchemePresenter.prepareFailView("Invalid hex color format. Please use 6-digit hex code (e.g., FF5733).");
            return;
        }

        try {
            List<String> monochromaticColors = colorApiDataAccessObject.getMonochromaticScheme(hexColor);
            List<String> analogousColors = colorApiDataAccessObject.getAnalogousScheme(hexColor);
            List<String> complementaryColors = colorApiDataAccessObject.getComplementaryScheme(hexColor);
            List<String> neutralColors = colorApiDataAccessObject.getNeutralScheme(hexColor);

            GetColorSchemeOutputData outputData = new GetColorSchemeOutputData(
                    monochromaticColors,
                    analogousColors,
                    complementaryColors,
                    neutralColors
            );

            colorSchemePresenter.prepareSuccessView(outputData);
        } catch (Exception e) {
            colorSchemePresenter.prepareFailView("Failed to fetch color schemes: " + e.getMessage());
        }
    }
}

