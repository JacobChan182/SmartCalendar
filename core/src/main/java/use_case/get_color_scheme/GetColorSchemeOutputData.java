package use_case.get_color_scheme;

import java.util.List;

/**
 * Output data for the Get Color Scheme Use Case.
 */
public class GetColorSchemeOutputData {
    private final List<String> monochromaticColors;
    private final List<String> analogousColors;
    private final List<String> complementaryColors;
    private final List<String> neutralColors;

    public GetColorSchemeOutputData(List<String> monochromaticColors,
                                    List<String> analogousColors,
                                    List<String> complementaryColors,
                                    List<String> neutralColors) {
        this.monochromaticColors = monochromaticColors;
        this.analogousColors = analogousColors;
        this.complementaryColors = complementaryColors;
        this.neutralColors = neutralColors;
    }

    public List<String> getMonochromaticColors() {
        return monochromaticColors;
    }

    public List<String> getAnalogousColors() {
        return analogousColors;
    }

    public List<String> getComplementaryColors() {
        return complementaryColors;
    }

    public List<String> getNeutralColors() {
        return neutralColors;
    }
}

