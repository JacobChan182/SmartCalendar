package use_case.get_current_weather;

import entity.WeatherInfo;

/**
 * Response model carrying the core WeatherInfo entity.
 */
public class GetCurrentWeatherResponseModel {
    private final WeatherInfo weatherInfo;

    public GetCurrentWeatherResponseModel(WeatherInfo weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public WeatherInfo getWeatherInfo() {
        return weatherInfo;
    }
}

