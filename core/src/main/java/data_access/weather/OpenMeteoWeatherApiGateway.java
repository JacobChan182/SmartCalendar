package data_access.weather;

import entity.WeatherInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.get_current_weather.WeatherApiGateway;
import use_case.get_current_weather.WeatherGatewayException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Weather API gateway using Open-Meteo geocoding + forecast APIs.
 */
public class OpenMeteoWeatherApiGateway implements WeatherApiGateway {

    private static final String GEOCODING_BASE =
            "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_BASE =
            "https://api.open-meteo.com/v1/forecast";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public WeatherInfo getCurrentWeather(String city, String country)
            throws WeatherGatewayException {

        if (city == null || city.isBlank()) {
            throw new WeatherGatewayException("City must not be empty.");
        }

        try {
            // ---- 1) Geocoding: city + optional country ----
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            StringBuilder geoUrl = new StringBuilder(GEOCODING_BASE)
                    .append("?name=").append(encodedCity)
                    .append("&count=1&language=en&format=json");

            if (country != null && !country.isBlank()) {
                String encodedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8);
                geoUrl.append("&country=").append(encodedCountry);
            }

            Request geoRequest = new Request.Builder()
                    .url(geoUrl.toString())
                    .build();

            double latitude;
            double longitude;
            String resolvedName;

            try (Response response = client.newCall(geoRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new WeatherGatewayException(
                            "Geocoding API error: HTTP " + response.code());
                }

                String body = response.body().string();
                JSONObject json = new JSONObject(body);

                if (!json.has("results") || json.getJSONArray("results").isEmpty()) {
                    throw new WeatherGatewayException(
                            "No location found for " + city +
                                    (country == null || country.isBlank() ? "" : ", " + country));
                }

                JSONObject first = json.getJSONArray("results").getJSONObject(0);
                latitude = first.getDouble("latitude");
                longitude = first.getDouble("longitude");
                resolvedName = first.getString("name");
                if (first.has("country")) {
                    resolvedName += ", " + first.getString("country");
                }
            }

            // ---- 2) Forecast API ----
            String forecastUrl = FORECAST_BASE
                    + "?latitude=" + latitude
                    + "&longitude=" + longitude
                    + "&current_weather=true"
                    + "&daily=temperature_2m_max,temperature_2m_min,weathercode"
                    + "&timezone=auto";

            Request forecastRequest = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            try (Response response = client.newCall(forecastRequest).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new WeatherGatewayException(
                            "Forecast API error: HTTP " + response.code());
                }

                String body = response.body().string();
                JSONObject json = new JSONObject(body);

                JSONObject current = json.getJSONObject("current_weather");
                JSONObject daily = json.getJSONObject("daily");

                double currentTemp = current.getDouble("temperature");
                int weatherCode = current.getInt("weathercode");
                boolean isDay = current.getInt("is_day") == 1;

                double minToday = daily.getJSONArray("temperature_2m_min").getDouble(0);
                double maxToday = daily.getJSONArray("temperature_2m_max").getDouble(0);

                return new WeatherInfo(
                        resolvedName,
                        currentTemp,
                        minToday,
                        maxToday,
                        weatherCode,
                        isDay
                );
            }

        } catch (IOException | JSONException e) {
            throw new WeatherGatewayException("Error calling Open-Meteo API", e);
        }
    }
}
