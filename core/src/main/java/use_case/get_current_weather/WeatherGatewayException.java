package use_case.get_current_weather;

/**
 * Exception thrown by the WeatherApiGateway when something goes wrong
 * while calling the external weather API (network error, bad response, etc.).
 */
public class WeatherGatewayException extends Exception {

    public WeatherGatewayException(String message) {
        super(message);
    }

    public WeatherGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
