package entity;

/**
 * Core weather entity representing the data the application cares about:
 * current temperature, today's min/max, and a simple code for conditions.
 */
public class WeatherInfo {
    private final String locationName;
    private final double currentTempC;
    private final double todayMaxTempC;
    private final double todayMinTempC;
    private final int weatherCode;   // Open-Meteo weather code
    private final boolean isDay;     // true if it is currently daytime

    public WeatherInfo(String locationName,
                       double currentTempC,
                       double todayMaxTempC,
                       double todayMinTempC,
                       int weatherCode,
                       boolean isDay) {
        this.locationName = locationName;
        this.currentTempC = currentTempC;
        this.todayMaxTempC = todayMaxTempC;
        this.todayMinTempC = todayMinTempC;
        this.weatherCode = weatherCode;
        this.isDay = isDay;
    }

    public String getLocationName() {
        return locationName;
    }

    public double getCurrentTempC() {
        return currentTempC;
    }

    public double getTodayMaxTempC() {
        return todayMaxTempC;
    }

    public double getTodayMinTempC() {
        return todayMinTempC;
    }

    public int getWeatherCode() {
        return weatherCode;
    }

    public boolean isDay() {
        return isDay;
    }
}
