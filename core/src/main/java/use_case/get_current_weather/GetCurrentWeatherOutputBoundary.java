package use_case.get_current_weather;

/**
 * Output boundary for the Get Current Weather use case.
 */
public interface GetCurrentWeatherOutputBoundary {
    void present(GetCurrentWeatherResponseModel responseModel);
    void presentError(String errorMessage);
}
