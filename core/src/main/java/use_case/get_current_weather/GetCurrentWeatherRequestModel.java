package use_case.get_current_weather;

/**
 * Request model for the Get Current Weather use case.
 * Holds the city and country entered by the user.
 */
public class GetCurrentWeatherRequestModel {

    private final String city;
    private final String country;

    public GetCurrentWeatherRequestModel(String city, String country) {
        this.city = city;
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }
}
