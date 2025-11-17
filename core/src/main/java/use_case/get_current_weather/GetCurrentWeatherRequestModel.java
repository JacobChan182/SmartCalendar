package use_case.get_current_weather;

/**
 * Request model containing the user-provided address string.
 */
public class GetCurrentWeatherRequestModel {
    private final String address;

    public GetCurrentWeatherRequestModel(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}
