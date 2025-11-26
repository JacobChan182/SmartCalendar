package interface_adapter.color_scheme;

import use_case.get_color_scheme.GetColorSchemeOutputBoundary;
import use_case.get_color_scheme.GetColorSchemeOutputData;

/**
 * The Presenter for the Get Color Scheme Use Case.
 */
public class ColorSchemePresenter implements GetColorSchemeOutputBoundary {

    private final ColorSchemeViewModel colorSchemeViewModel;

    public ColorSchemePresenter(ColorSchemeViewModel colorSchemeViewModel) {
        this.colorSchemeViewModel = colorSchemeViewModel;
    }

    @Override
    public void prepareSuccessView(GetColorSchemeOutputData outputData) {
        ColorSchemeState state = colorSchemeViewModel.getState();
        state.setMonochromaticColors(outputData.getMonochromaticColors());
        state.setAnalogousColors(outputData.getAnalogousColors());
        state.setComplementaryColors(outputData.getComplementaryColors());
        state.setNeutralColors(outputData.getNeutralColors());
        state.setErrorMessage(null);
        colorSchemeViewModel.firePropertyChange();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        ColorSchemeState state = colorSchemeViewModel.getState();
        state.setErrorMessage(errorMessage);
        // Clear color lists on error
        state.setMonochromaticColors(new java.util.ArrayList<>());
        state.setAnalogousColors(new java.util.ArrayList<>());
        state.setComplementaryColors(new java.util.ArrayList<>());
        state.setNeutralColors(new java.util.ArrayList<>());
        colorSchemeViewModel.firePropertyChange();
    }
}

