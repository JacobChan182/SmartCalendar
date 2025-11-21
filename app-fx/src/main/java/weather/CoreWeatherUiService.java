package weather;

import data_access.weather.OpenMeteoWeatherApiGateway;
import interface_adapter.controllers.GetCurrentWeatherController;
import interface_adapter.presenters.GetCurrentWeatherPresenter;
import interface_adapter.presenters.WeatherViewModel;
import use_case.get_current_weather.GetCurrentWeatherInteractor;
import use_case.get_current_weather.WeatherApiGateway;

/**
 * Concrete implementation of WeatherUiService that delegates
 * to the core Clean Architecture weather stack.
 */
public class CoreWeatherUiService implements WeatherUiService {

    private final WeatherViewModel viewModel;
    private final GetCurrentWeatherController controller;

    /**
     * Default constructor that wires up the whole weather use case
     * using the production Open-Meteo API gateway.
     *
     * In tests you could create another constructor and inject fakes.
     */
    public CoreWeatherUiService() {
        this(buildDefaultControllerAndViewModel());
    }

    private CoreWeatherUiService(ControllerAndViewModel bundle) {
        this.controller = bundle.controller;
        this.viewModel = bundle.viewModel;
    }

    /**
     * Small bundle type to return both controller and view model together.
     */
    private static class ControllerAndViewModel {
        final GetCurrentWeatherController controller;
        final WeatherViewModel viewModel;

        ControllerAndViewModel(GetCurrentWeatherController controller,
                               WeatherViewModel viewModel) {
            this.controller = controller;
            this.viewModel = viewModel;
        }
    }

    /**
     * Wire the full Clean Architecture chain:
     * UI -> Controller -> Interactor -> WeatherApiGateway & Presenter -> ViewModel
     */
    private static ControllerAndViewModel buildDefaultControllerAndViewModel() {
        WeatherViewModel viewModel = new WeatherViewModel();
        GetCurrentWeatherPresenter presenter = new GetCurrentWeatherPresenter(viewModel);

        WeatherApiGateway gateway = new OpenMeteoWeatherApiGateway();
        GetCurrentWeatherInteractor interactor =
                new GetCurrentWeatherInteractor(gateway, presenter);

        GetCurrentWeatherController controller =
                new GetCurrentWeatherController(interactor);

        return new ControllerAndViewModel(controller, viewModel);
    }

    // -------- WeatherUiService implementation --------

    @Override
    public void fetchWeather(String city, String country) {
        controller.onGetCurrentWeather(city, country);
    }

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
