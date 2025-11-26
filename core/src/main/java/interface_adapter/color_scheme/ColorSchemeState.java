package interface_adapter.color_scheme;

import java.util.ArrayList;
import java.util.List;

/**
 * The state for the Color Scheme View Model.
 */
public class ColorSchemeState {
    private String hexColor = "";
    private String errorMessage = null;
    private List<String> monochromaticColors = new ArrayList<>();
    private List<String> analogousColors = new ArrayList<>();
    private List<String> complementaryColors = new ArrayList<>();
    private List<String> neutralColors = new ArrayList<>();

    public String getHexColor() {
        return hexColor;
    }

    public void setHexColor(String hexColor) {
        this.hexColor = hexColor;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<String> getMonochromaticColors() {
        return monochromaticColors;
    }

    public void setMonochromaticColors(List<String> monochromaticColors) {
        this.monochromaticColors = monochromaticColors;
    }

    public List<String> getAnalogousColors() {
        return analogousColors;
    }

    public void setAnalogousColors(List<String> analogousColors) {
        this.analogousColors = analogousColors;
    }

    public List<String> getComplementaryColors() {
        return complementaryColors;
    }

    public void setComplementaryColors(List<String> complementaryColors) {
        this.complementaryColors = complementaryColors;
    }

    public List<String> getNeutralColors() {
        return neutralColors;
    }

    public void setNeutralColors(List<String> neutralColors) {
        this.neutralColors = neutralColors;
    }
}

