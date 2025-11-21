package weather;

/**
 * Simple CLI main to manually test the WeatherUiService.
 *
 * Put this file under:
 *   app-fx/src/main/java/weather/WeatherUiServiceDebugMain.java
 *
 * Then run it as a normal Java application.
 */
public class WeatherUiServiceDebugMain {

    public static void main(String[] args) {
        // 1. Choose city & country to test
        String city = args.length > 0 ? args[0] : "Toronto";
        String country = args.length > 1 ? args[1] : "Canada";

        System.out.println("Testing WeatherUiService for: "
                + city + ", " + country);

        // 2. Create the UI service (this will wire into core module)
        WeatherUiService service = new CoreWeatherUiService();

        // 3. Call the interface
        service.fetchWeather(city, country);

        // 4. Read results and print
        String error = service.getError();
        if (error != null && !error.isBlank()) {
            System.out.println("=== ERROR ===");
            System.out.println(error);
        } else {
            System.out.println("=== SUCCESS ===");
            System.out.println("Location:    " + service.getLocationDisplay());
            System.out.println("Current:     " + service.getCurrentTempText());
            System.out.println("Today range: " + service.getTodayRangeText());
            System.out.println("Conditions:  " + service.getDescriptionText());
        }
    }
}

