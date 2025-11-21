package weatherdebug;

import data_access.weather.OpenMeteoWeatherApiGateway;
import interface_adapter.controllers.GetCurrentWeatherController;
import interface_adapter.presenters.GetCurrentWeatherPresenter;
import interface_adapter.presenters.WeatherViewModel;
import use_case.get_current_weather.GetCurrentWeatherInteractor;
import use_case.get_current_weather.WeatherApiGateway;

/**
 * Very simple temporary main for quickly testing the weather feature.
 * Uses city + country instead of a single address string.
 */
public class WeatherQuickMain {

    public static void main(String[] args) {
        // 1. Wire up the CA stack
        WeatherViewModel viewModel = new WeatherViewModel();
        GetCurrentWeatherPresenter presenter = new GetCurrentWeatherPresenter(viewModel);

        WeatherApiGateway gateway = new OpenMeteoWeatherApiGateway();
        GetCurrentWeatherInteractor interactor =
                new GetCurrentWeatherInteractor(gateway, presenter);

        GetCurrentWeatherController controller =
                new GetCurrentWeatherController(interactor);

        // 2. Test input: city + country
        String city = "Toronto";
        String country = "Canada";

        // 3. Trigger the use case
        controller.onGetCurrentWeather(city, country);

        // 4. Print what the UI would show
        if (viewModel.getError() != null) {
            System.out.println("Error: " + viewModel.getError());
        } else {
            System.out.println("Location:    " + viewModel.getLocationDisplay());
            System.out.println("Current:     " + viewModel.getCurrentTempText());
            System.out.println("Today range: " + viewModel.getTodayRangeText());
            System.out.println("Conditions:  " + viewModel.getDescriptionText());
        }
    }
}
