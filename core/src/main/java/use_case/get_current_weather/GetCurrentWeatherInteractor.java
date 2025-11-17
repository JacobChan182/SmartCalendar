package use_case.get_current_weather;

import entity.Location;
import entity.WeatherInfo;

/**
 * Interactor for the Get Current Weather use case.
 * This is the application/business logic that orchestrates the flow.
 */
public class GetCurrentWeatherInteractor implements GetCurrentWeatherInputBoundary {

    private final WeatherApiGateway weatherApiGateway;
    private final GetCurrentWeatherOutputBoundary presenter;

    public GetCurrentWeatherInteractor(WeatherApiGateway weatherApiGateway,
                                       GetCurrentWeatherOutputBoundary presenter) {
        this.weatherApiGateway = weatherApiGateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetCurrentWeatherRequestModel requestModel) {
        String address = requestModel.getAddress();

        if (address == null || address.isBlank()) {
            presenter.presentError("Address must not be empty.");
            return;
        }

        try {
            // 1. Address -> Location (via geocoding)
            Location location = weatherApiGateway.resolveLocation(address);

            // 2. Location -> WeatherInfo (current + today's min/max)
            WeatherInfo weatherInfo = weatherApiGateway.getCurrentWeather(location);

            presenter.present(new GetCurrentWeatherResponseModel(weatherInfo));

        } catch (WeatherGatewayException e) {
            presenter.presentError("Failed to fetch weather: " + e.getMessage());
        }
    }
}

