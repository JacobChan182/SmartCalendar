package use_case.get_current_weather;

/**
 * Input boundary for the "Get Current Weather" use case.
 */
public interface GetCurrentWeatherInputBoundary {

    /**
     * Execute the use case with the given request model.
     */
    void execute(GetCurrentWeatherRequestModel requestModel);
}
