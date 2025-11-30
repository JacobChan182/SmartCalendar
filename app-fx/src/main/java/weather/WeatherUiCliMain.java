package weather;

import java.util.Scanner;

/**
 * Simple CLI main to test WeatherUiService interactively.
 *
 * Run this class as a normal Java application.
 * It will:
 *   1. Ask you to enter a city
 *   2. Ask you to enter a country or country code
 *   3. Call WeatherUiService and print the result
 */
public class WeatherUiCliMain {

    public static void main(String[] args) {
        // UI-facing service that internally wires controller/interactor/gateway
        WeatherUiService service = new CoreWeatherUiService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Weather CLI Test ===");
        System.out.println("Type 'exit' at any prompt to quit.\n");

        while (true) {
            // 1. Read city
            System.out.print("Enter city (e.g. Toronto): ");
            String city = scanner.nextLine().trim();
            if (city.equalsIgnoreCase("exit")) {
                System.out.println("Bye ~");
                break;
            }

            // 2. Read country
            System.out.print("Enter country or country code (e.g. Canada or CA): ");
            String country = scanner.nextLine().trim();
            if (country.equalsIgnoreCase("exit")) {
                System.out.println("Bye ~");
                break;
            }

            if (city.isEmpty() || country.isEmpty()) {
                System.out.println("City and country must not be empty. Please try again.\n");
                continue;
            }

            // 3. Trigger the use case via the UI service
            System.out.println("\nFetching weather for: " + city + ", " + country + " ...");
            service.fetchWeather(city, country);

            // 4. Read and print results
            String error = service.getError();
            if (error != null && !error.isBlank()) {
                System.out.println("=== FAILED ===");
                System.out.println(error);
            } else {
                System.out.println("=== SUCCESS ===");
                System.out.println("Location:    " + service.getLocationDisplay());
                System.out.println("Current:     " + service.getCurrentTempText());
                System.out.println("Today range: " + service.getTodayRangeText());
                System.out.println("Conditions:  " + service.getDescriptionText());
            }
            System.out.println(); // blank line before next loop
        }

        scanner.close();
    }
}
