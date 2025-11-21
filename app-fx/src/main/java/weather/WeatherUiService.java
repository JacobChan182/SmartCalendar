package weather;

/**
 * Simple interface that the JavaFX UI can use to work with the
 * "get current weather" feature.
 *
 * The UI should only depend on this interface and not know anything
 * about controllers, interactors, or API gateways in the core module.
 */
public interface WeatherUiService {

    /**
     * Trigger a refresh of the weather for the given city and country.
     *
     * @param city    city name, e.g. "Toronto"
     * @param country country name or ISO code, e.g. "Canada" or "CA".
     *                May be null/blank if the user only enters a city.
     */
    void fetchWeather(String city, String country);

    /** @return The location text to display, e.g. "Toronto, Canada". */
    String getLocationDisplay();

    /** @return A text like "Current: 5.3 °C". */
    String getCurrentTempText();

    /** @return A text like "Today: -1.0 °C ~ 7.2 °C". */
    String getTodayRangeText();

    /** @return A text describing the conditions, e.g. "Partly cloudy". */
    String getDescriptionText();

    /** @return Error message if something went wrong, or null if everything is OK. */
    String getError();
}
