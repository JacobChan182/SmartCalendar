package use_case.get_color_scheme;

import java.util.List;

/**
 * Data Access Interface for the Get Color Scheme Use Case.
 */
public interface GetColorSchemeUserDataAccessInterface {
    /**
     * Gets a monochromatic color scheme for the given hex color.
     * @param hexColor the hex color (without #)
     * @return list of 5 hex color codes
     */
    List<String> getMonochromaticScheme(String hexColor);

    /**
     * Gets an analogous color scheme for the given hex color.
     * @param hexColor the hex color (without #)
     * @return list of 5 hex color codes
     */
    List<String> getAnalogousScheme(String hexColor);

    /**
     * Gets a complementary color scheme for the given hex color.
     * @param hexColor the hex color (without #)
     * @return list of 5 hex color codes
     */
    List<String> getComplementaryScheme(String hexColor);

    /**
     * Gets a neutral (monochrome-light) color scheme for the given hex color.
     * @param hexColor the hex color (without #)
     * @return list of 5 hex color codes
     */
    List<String> getNeutralScheme(String hexColor);
}

