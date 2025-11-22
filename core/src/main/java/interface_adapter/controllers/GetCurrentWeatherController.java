package interface_adapter.controllers;

import use_case.get_current_weather.GetCurrentWeatherInputBoundary;
import use_case.get_current_weather.GetCurrentWeatherRequestModel;

/**
 * Controller called by the UI / UI services to trigger the use case.
 */
public class GetCurrentWeatherController {

    private final GetCurrentWeatherInputBoundary interactor;

    public GetCurrentWeatherController(GetCurrentWeatherInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Called when the user requests weather for a given city and country.
     */
    public void onGetCurrentWeather(String city, String country) {
        GetCurrentWeatherRequestModel request =
                new GetCurrentWeatherRequestModel(city, country);
        interactor.execute(request);
    }
}
