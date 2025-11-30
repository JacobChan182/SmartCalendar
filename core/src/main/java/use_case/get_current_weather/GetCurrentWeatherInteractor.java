package use_case.get_current_weather;

import entity.WeatherInfo;

/**
 * Interactor for the "Get Current Weather" use case.
 *
 * Flow:
 *   controller -> interactor (this class) -> WeatherApiGateway & presenter
 */
public class GetCurrentWeatherInteractor implements GetCurrentWeatherInputBoundary {

    private final WeatherApiGateway gateway;
    private final GetCurrentWeatherOutputBoundary presenter;

    public GetCurrentWeatherInteractor(WeatherApiGateway gateway,
                                       GetCurrentWeatherOutputBoundary presenter) {
        this.gateway = gateway;
        this.presenter = presenter;
    }

    @Override
    public void execute(GetCurrentWeatherRequestModel requestModel) {
        try {
            // 1. 调用网关，根据 city + country 获取天气数据
            WeatherInfo info = gateway.getCurrentWeather(
                    requestModel.getCity(),
                    requestModel.getCountry()
            );

            // 2. 包装成 response model
            GetCurrentWeatherResponseModel response =
                    new GetCurrentWeatherResponseModel(info);

            // 3. 交给 presenter 更新 ViewModel
            presenter.present(response);

        } catch (WeatherGatewayException e) {
            // 4. 出错时，把错误信息交给 presenter
            presenter.presentError("Failed to fetch weather: " + e.getMessage());
        }
    }
}
