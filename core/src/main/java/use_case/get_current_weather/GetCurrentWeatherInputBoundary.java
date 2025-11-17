package use_case.get_current_weather;

/**
 * Input boundary for the Get Current Weather use case.
 */
public interface GetCurrentWeatherInputBoundary {
    void execute(GetCurrentWeatherRequestModel requestModel);
}
