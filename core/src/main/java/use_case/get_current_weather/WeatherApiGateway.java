package use_case.get_current_weather;

import entity.WeatherInfo;

/**
 * Port used by the interactor to fetch weather data from an external API.
 */
public interface WeatherApiGateway {

    /**
     * Fetch current weather for a given city and country.
     *
     * @param city    city name
     * @param country country name or ISO code (may be null/blank)
     */
    WeatherInfo getCurrentWeather(String city, String country)
            throws WeatherGatewayException;
}
