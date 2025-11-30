package entities;

public class Weather {
    private final String condition; // e.g., "Sunny", "Rainy"
    private final double temperatureCelsius;
    private final double windSpeedKmh;

    public Weather(String condition, double temperatureCelsius, double windSpeedKmh) {
        this.condition = condition;
        this.temperatureCelsius = temperatureCelsius;
        this.windSpeedKmh = windSpeedKmh;
    }

    public String getCondition() { return condition; }
    public double getTemperatureCelsius() { return temperatureCelsius; }
    public double getWindSpeedKmh() { return windSpeedKmh; }
}
