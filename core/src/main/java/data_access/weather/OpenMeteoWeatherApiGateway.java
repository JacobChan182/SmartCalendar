package data_access.weather;

import entity.Location;
import entity.WeatherInfo;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import use_case.get_current_weather.WeatherApiGateway;
import use_case.get_current_weather.WeatherGatewayException;

import java.io.IOException;

/**
 * Open-Meteo implementation of the WeatherApiGateway.
 * This lives in the data_access layer and knows about HTTP/JSON.
 */
public class OpenMeteoWeatherApiGateway implements WeatherApiGateway {

    private static final String GEO_BASE_URL =
            "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_BASE_URL =
            "https://api.open-meteo.com/v1/forecast";

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public Location resolveLocation(String address) throws WeatherGatewayException {
        try {
            HttpUrl url = HttpUrl.parse(GEO_BASE_URL)
                    .newBuilder()
                    .addQueryParameter("name", address)
                    .addQueryParameter("count", "1")
                    .addQueryParameter("language", "en")
                    .addQueryParameter("format", "json")
                    .build();

            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new WeatherGatewayException(
                            "Geocoding API call failed with HTTP code: " + response.code());
                }

                String body = response.body().string();
                JSONObject json = new JSONObject(body);
                JSONArray results = json.optJSONArray("results");

                if (results == null || results.length() == 0) {
                    throw new WeatherGatewayException(
                            "No location results found for address: " + address);
                }

                JSONObject first = results.getJSONObject(0);
                double lat = first.getDouble("latitude");
                double lon = first.getDouble("longitude");
                String name = first.getString("name");
                String country = first.optString("country", "");

                String displayName = country.isEmpty()
                        ? name
                        : name + ", " + country;

                return new Location(displayName, lat, lon);
            }
        } catch (IOException | JSONException e) {
            throw new WeatherGatewayException("Failed to parse geocoding response: "
                    + e.getMessage(), e);
        }
    }

    @Override
    public WeatherInfo getCurrentWeather(Location location)
            throws WeatherGatewayException {
        try {
            HttpUrl url = HttpUrl.parse(FORECAST_BASE_URL)
                    .newBuilder()
                    .addQueryParameter("latitude", String.valueOf(location.getLatitude()))
                    .addQueryParameter("longitude", String.valueOf(location.getLongitude()))
                    .addQueryParameter("current_weather", "true")
                    .addQueryParameter("daily",
                            "weathercode,temperature_2m_max,temperature_2m_min")
                    .addQueryParameter("forecast_days", "1")
                    .addQueryParameter("timezone", "auto")
                    .build();

            Request request = new Request.Builder().url(url).build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new WeatherGatewayException(
                            "Forecast API call failed with HTTP code: " + response.code());
                }

                String body = response.body().string();
                JSONObject json = new JSONObject(body);

                JSONObject current = json.getJSONObject("current_weather");
                double currentTemp = current.getDouble("temperature");
                int weatherCode = current.getInt("weathercode");
                boolean isDay = current.optInt("is_day", 1) == 1;

                JSONObject daily = json.getJSONObject("daily");
                JSONArray maxArr = daily.getJSONArray("temperature_2m_max");
                JSONArray minArr = daily.getJSONArray("temperature_2m_min");

                if (maxArr.isEmpty() || minArr.isEmpty()) {
                    throw new WeatherGatewayException("Daily temperature arrays are empty");
                }

                double todayMax = maxArr.getDouble(0);
                double todayMin = minArr.getDouble(0);

                return new WeatherInfo(
                        location.getDisplayName(),
                        currentTemp,
                        todayMax,
                        todayMin,
                        weatherCode,
                        isDay
                );
            }
        } catch (IOException | JSONException e) {
            throw new WeatherGatewayException("Failed to parse weather response: "
                    + e.getMessage(), e);
        }
    }
}
