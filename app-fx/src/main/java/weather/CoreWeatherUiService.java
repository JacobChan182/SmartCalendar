package weather;

import data_access.weather.OpenMeteoWeatherApiGateway;
import interface_adapter.controllers.GetCurrentWeatherController;
import interface_adapter.presenters.GetCurrentWeatherPresenter;
import interface_adapter.presenters.WeatherViewModel;
import use_case.get_current_weather.GetCurrentWeatherInteractor;
import use_case.get_current_weather.WeatherApiGateway;

/**
 * CoreWeatherUiService is the bridge between the JavaFX UI
 * and the clean-architecture weather use case.
 *
 * The UI talks only to this class via the WeatherUiService interface.
 * This class wires:
 *   UI -> Controller -> Interactor -> Gateway -> Presenter -> ViewModel
 */
public class CoreWeatherUiService implements WeatherUiService {

    private final WeatherViewModel viewModel;
    private final GetCurrentWeatherController controller;

    /**
     * Default constructor used by the JavaFX layer.
     * It creates and wires all the dependencies needed for the use case.
     */
    public CoreWeatherUiService() {
        // 1. Create the view model that will hold display strings.
        this.viewModel = new WeatherViewModel();

        // 2. Presenter writes into the view model.
        GetCurrentWeatherPresenter presenter = new GetCurrentWeatherPresenter(viewModel);

        // 3. Gateway calls the Open-Meteo API.
        WeatherApiGateway gateway = new OpenMeteoWeatherApiGateway();

        // 4. Interactor is the use-case logic.
        GetCurrentWeatherInteractor interactor =
                new GetCurrentWeatherInteractor(gateway, presenter);

        // 5. Controller is called by this UI service.
        this.controller = new GetCurrentWeatherController(interactor);
    }

    /**
     * Trigger the use case with the given city and country.
     * This is the only method the UI needs to call to fetch weather.
     */
    @Override
    public void fetchWeather(String city, String country) {
        // ✅ IMPORTANT: use the two-argument version of the controller method
        controller.onGetCurrentWeather(city, country);
    }

    // --- Getter methods: the UI reads from the view model through this service ---

    @Override
    public String getLocationDisplay() {
        return viewModel.getLocationDisplay();
    }

    @Override
    public String getCurrentTempText() {
        return viewModel.getCurrentTempText();
    }

    @Override
    public String getTodayRangeText() {
        return viewModel.getTodayRangeText();
    }

    @Override
    public String getDescriptionText() {
        return viewModel.getDescriptionText();
    }

    @Override
    public String getError() {
        return viewModel.getError();
    }
}
