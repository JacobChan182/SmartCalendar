package data_access;

import use_case.get_color_scheme.GetColorSchemeUserDataAccessInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory mock implementation of the Color API Data Access Interface.
 * This implementation returns predefined color schemes for testing purposes
 * without making actual HTTP requests.
 */
public class InMemoryColorApiDataAccessObject implements GetColorSchemeUserDataAccessInterface {

    @Override
    public List<String> getMonochromaticScheme(String hexColor) {
        return generateMockScheme(hexColor, "mono");
    }

    @Override
    public List<String> getAnalogousScheme(String hexColor) {
        return generateMockScheme(hexColor, "analog");
    }

    @Override
    public List<String> getComplementaryScheme(String hexColor) {
        return generateMockScheme(hexColor, "comp");
    }

    @Override
    public List<String> getNeutralScheme(String hexColor) {
        return generateMockScheme(hexColor, "neutral");
    }

    /**
     * Generates a mock color scheme with 5 colors.
     * In a real implementation, this would call The Color API.
     * 
     * @param hexColor the base hex color
     * @param schemeType the type of scheme (for generating different colors)
     * @return list of 5 hex color codes
     */
    private List<String> generateMockScheme(String hexColor, String schemeType) {
        List<String> colors = new ArrayList<>();
        colors.add(hexColor); // First color is the input color
        
        // Generate 4 more colors based on the scheme type
        // This is a simplified mock - in reality, these would be calculated
        // based on color theory or fetched from the API
        for (int i = 1; i <= 4; i++) {
            // Create variations by modifying the hex slightly
            // This is just for testing - not real color theory
            String variation = generateColorVariation(hexColor, i, schemeType);
            colors.add(variation);
        }
        
        return colors;
    }

    /**
     * Generates a color variation for testing purposes.
     * This is a simplified mock implementation.
     */
    private String generateColorVariation(String baseHex, int offset, String schemeType) {
        // Simple mock: just modify the last digit
        // In reality, this would use proper color theory
        int lastDigit = Integer.parseInt(baseHex.substring(5), 16);
        int newDigit = (lastDigit + offset * 10) % 16;
        return baseHex.substring(0, 5) + Integer.toHexString(newDigit).toUpperCase();
    }
}

