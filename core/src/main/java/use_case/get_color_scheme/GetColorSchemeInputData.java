package use_case.get_color_scheme;

/**
 * Input data for the Get Color Scheme Use Case.
 */
public class GetColorSchemeInputData {
    private final String hexColor;

    public GetColorSchemeInputData(String hexColor) {
        this.hexColor = hexColor;
    }

    public String getHexColor() {
        return hexColor;
    }
}

