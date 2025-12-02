package use_case.weather;

import entity.WeatherInfo;
import org.junit.jupiter.api.Test;
import use_case.get_current_weather.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for GetCurrentWeatherInteractor.
 *
 * We cover:
 *  - success path  (gateway returns WeatherInfo -> presenter.present)
 *  - failure path  (gateway throws WeatherGatewayException -> presenter.presentError)
 *
 * This gives 100% coverage for GetCurrentWeatherInteractor.
 */
class GetCurrentWeatherInteractorTest {

    /**
     * Fake gateway that always returns a fixed WeatherInfo.
     * Used to test the normal "success" path.
     */
    private static class FakeSuccessGateway implements WeatherApiGateway {

        @Override
        public WeatherInfo getCurrentWeather(String city, String country)
                throws WeatherGatewayException {

            // 构造一个假的 WeatherInfo：
            // (locationName, currentTempC, todayMaxTempC, todayMinTempC, weatherCode, isDay)
            return new WeatherInfo(
                    city + ", " + country,
                    10.0,
                    15.0,
                    5.0,
                    2,
                    true
            );
        }
    }

    /**
     * Fake gateway that always throws an exception.
     * Used to test the error path.
     */
    private static class FakeFailGateway implements WeatherApiGateway {

        @Override
        public WeatherInfo getCurrentWeather(String city, String country)
                throws WeatherGatewayException {
            throw new WeatherGatewayException("Network error");
        }
    }

    /**
     * Presenter implementation that just records what it was called with.
     */
    private static class RecordingPresenter implements GetCurrentWeatherOutputBoundary {

        GetCurrentWeatherResponseModel lastResponse;
        String lastError;

        @Override
        public void present(GetCurrentWeatherResponseModel responseModel) {
            this.lastResponse = responseModel;
        }

        @Override
        public void presentError(String errorMessage) {
            this.lastError = errorMessage;
        }
    }

    @Test
    void execute_success_callsPresenterWithResponse() {
        // Arrange
        WeatherApiGateway gateway = new FakeSuccessGateway();
        RecordingPresenter presenter = new RecordingPresenter();
        GetCurrentWeatherInputBoundary interactor =
                new GetCurrentWeatherInteractor(gateway, presenter);

        GetCurrentWeatherRequestModel request =
                new GetCurrentWeatherRequestModel("Toronto", "Canada");

        // Act
        interactor.execute(request);

        // Assert: 走成功分支 -> 调用了 present()，没有错误
        assertNotNull(presenter.lastResponse,
                "Presenter should receive a response model on success");
        assertNull(presenter.lastError,
                "Error message should be null on success");

        // 再检查一下 WeatherInfo 里的字段
        WeatherInfo info = presenter.lastResponse.getWeatherInfo();
        assertEquals("Toronto, Canada", info.getLocationName());
        assertEquals(10.0, info.getCurrentTempC(), 1e-6);
        assertEquals(15.0, info.getTodayMaxTempC(), 1e-6);
        assertEquals(5.0, info.getTodayMinTempC(), 1e-6);
        assertEquals(2, info.getWeatherCode());
        assertTrue(info.isDay());
    }

    @Test
    void execute_gatewayThrows_callsPresenterError() {
        // Arrange
        WeatherApiGateway gateway = new FakeFailGateway();
        RecordingPresenter presenter = new RecordingPresenter();
        GetCurrentWeatherInputBoundary interactor =
                new GetCurrentWeatherInteractor(gateway, presenter);

        GetCurrentWeatherRequestModel request =
                new GetCurrentWeatherRequestModel("Toronto", "Canada");

        // Act
        interactor.execute(request);

        // Assert: 走异常分支 -> 调用了 presentError()，没有 response
        assertNull(presenter.lastResponse,
                "Response model should be null when gateway fails");
        assertNotNull(presenter.lastError,
                "Error message should be set when gateway fails");

        // 你的 interactor 写的是：
        // presenter.presentError("Failed to fetch weather: " + e.getMessage());
        assertTrue(
                presenter.lastError.startsWith("Failed to fetch weather:"),
                "Error message should be prefixed by interactor"
        );
        assertTrue(
                presenter.lastError.contains("Network error"),
                "Original gateway error message should be included"
        );
    }
}

