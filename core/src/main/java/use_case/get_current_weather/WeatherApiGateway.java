package use_case.get_current_weather;

import entity.Location;
import entity.WeatherInfo;

/**
 * Port interface for accessing weather data through an external API.
 * Implementations live in the frameworks_and_drivers layer.
 */
public interface WeatherApiGateway {

    /**
     * Resolve a city/address string to a geographic location.
     */
    Location resolveLocation(String address) throws WeatherGatewayException;

    /**
     * Fetch current weather and today's min/max temperature for a location.
     */
    WeatherInfo getCurrentWeather(Location location) throws WeatherGatewayException;
}
