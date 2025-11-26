package interface_adapter.presenters;

import entity.WeatherInfo;
import use_case.get_current_weather.GetCurrentWeatherOutputBoundary;
import use_case.get_current_weather.GetCurrentWeatherResponseModel;

/**
 * Presenter converts the use case response model into a view model
 * that is easy for the UI to display.
 */
public class GetCurrentWeatherPresenter implements GetCurrentWeatherOutputBoundary {

    private final WeatherViewModel viewModel;

    public GetCurrentWeatherPresenter(WeatherViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void present(GetCurrentWeatherResponseModel responseModel) {
        WeatherInfo info = responseModel.getWeatherInfo();

        viewModel.setError(null);
        viewModel.setLocationDisplay(info.getLocationName());
        viewModel.setCurrentTempText(
                String.format("Current: %.1f °C", info.getCurrentTempC())
        );
        viewModel.setTodayRangeText(
                String.format("Today: %.1f °C ~ %.1f °C",
                        info.getTodayMinTempC(), info.getTodayMaxTempC())
        );
        viewModel.setDescriptionText(
                mapWeatherCode(info.getWeatherCode(), info.isDay())
        );
    }

    @Override
    public void presentError(String errorMessage) {
        viewModel.setError(errorMessage);
        viewModel.setLocationDisplay(null);
        viewModel.setCurrentTempText(null);
        viewModel.setTodayRangeText(null);
        viewModel.setDescriptionText(null);
    }

    public WeatherViewModel getViewModel() {
        return viewModel;
    }

    /**
     * Map Open-Meteo weather codes to simple English descriptions.
     * This is a simplified mapping, sufficient for the assignment.
     */
    private String mapWeatherCode(int code, boolean isDay) {
        String desc;
        switch (code) {
            case 0:
                desc = isDay ? "Clear sky" : "Clear sky (night)";
                break;
            case 1, 2:
                desc = "Partly cloudy";
                break;
            case 3:
                desc = "Overcast";
                break;
            case 45, 48:
                desc = "Foggy";
                break;
            case 51, 53, 55:
                desc = "Drizzle";
                break;
            case 56, 57:
                desc = "Freezing drizzle";
                break;
            case 61, 63, 65:
                desc = "Rain";
                break;
            case 66, 67:
                desc = "Freezing rain";
                break;
            case 71, 73, 75:
                desc = "Snowfall";
                break;
            case 77:
                desc = "Snow grains";
                break;
            case 80, 81, 82:
                desc = "Rain showers";
                break;
            case 85, 86:
                desc = "Snow showers";
                break;
            case 95:
                desc = "Thunderstorm";
                break;
            case 96, 97:
                desc = "Thunderstorm with hail";
                break;
            default:
                desc = "Unknown conditions (code " + code + ")";
        }
        return desc;
    }
}
