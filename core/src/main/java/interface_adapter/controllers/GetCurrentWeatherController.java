package interface_adapter.controllers;

import use_case.get_current_weather.GetCurrentWeatherInputBoundary;
import use_case.get_current_weather.GetCurrentWeatherRequestModel;

/**
 * Controller called by the UI to trigger the Get Current Weather use case.
 */
public class GetCurrentWeatherController {

    private final GetCurrentWeatherInputBoundary interactor;

    public GetCurrentWeatherController(GetCurrentWeatherInputBoundary interactor) {
        this.interactor = interactor;
    }

    /**
     * Called by the UI when the user submits a city/address string.
     */
    public void onGetCurrentWeather(String address) {
        GetCurrentWeatherRequestModel request =
                new GetCurrentWeatherRequestModel(address);
        interactor.execute(request);
    }
}
