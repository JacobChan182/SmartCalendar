package use_case.get_current_weather;

/**
 * Exception type used when the weather gateway fails.
 */
public class WeatherGatewayException extends Exception {
    public WeatherGatewayException(String message) {
        super(message);
    }

    public WeatherGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
