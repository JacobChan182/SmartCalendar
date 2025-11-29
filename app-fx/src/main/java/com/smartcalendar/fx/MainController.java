package com.smartcalendar.fx;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.*;
import java.util.List;

// ===== Weather: UI-facing service interface =====
import weather.CoreWeatherUiService;
import weather.WeatherUiService;

public class MainController {

    // ================== Calendar UI controls ==================
    @FXML private Label lblYearMonth;
    @FXML private GridPane monthGrid;
    @FXML private Button btnPrev, btnNext;
    @FXML private ListView<String> dayEvents;

    // Current month being displayed & the selected date
    private YearMonth current = YearMonth.now();
    private LocalDate selected = LocalDate.now();

    // ================== WEATHER: UI fields & service ==================

    // User input fields for city and country (TextField must exist in FXML)
    @FXML private TextField cityField;      // e.g. "Toronto"
    @FXML private TextField countryField;   // e.g. "Canada" or "CA"

    // Labels to display weather results (must match Label fx:id in FXML)
    @FXML private Label weatherLocationLabel;    // "Toronto, Canada"
    @FXML private Label weatherCurrentLabel;     // "Current: 3.4 °C"
    @FXML private Label weatherRangeLabel;       // "Today: -1.0 °C ~ 6.2 °C"
    @FXML private Label weatherConditionLabel;   // "Partly cloudy"
    @FXML private Label weatherErrorLabel;       // Shows error message if something fails

    // Entry point from UI to core weather feature:
    // internally wires controller / interactor / gateway
    private final WeatherUiService weatherService = new CoreWeatherUiService();

    // ================== initialize (called automatically by JavaFX) ==================
    @FXML
    private void initialize() {
        // Previous / next month buttons
        btnPrev.setOnAction(e -> changeMonth(-1));
        btnNext.setOnAction(e -> changeMonth(1));

        // ⭐ Weather: optional default values in text fields for quick testing
        if (cityField != null && countryField != null) {
            cityField.setText("Toronto");
            countryField.setText("Canada");
        }

        // Render calendar & right-side “day details” list
        renderMonth();
        refreshDayDetails();
    }

    // ================== Calendar logic ==================

    /** Change the currently displayed month (delta can be -1 / +1 etc.). */
    private void changeMonth(int delta) {
        current = current.plusMonths(delta);
        selected = current.atDay(Math.min(
                selected.getDayOfMonth(), current.lengthOfMonth()));
        renderMonth();
    }

    /** Render the calendar grid according to the current YearMonth. */
    private void renderMonth() {
        lblYearMonth.setText(current.toString()); // e.g. "2025-11"
        monthGrid.getChildren().clear();

        // Weekday header row, Monday as the first day
        var headers = List.of("Mon","Tue","Wed","Thu","Fri","Sat","Sun");
        for (int c = 0; c < 7; c++) {
            var hdr = new Label(headers.get(c));
            hdr.getStyleClass().add("weekday");
            monthGrid.add(hdr, c, 0);
        }

        LocalDate first = current.atDay(1);
        int shift = (first.getDayOfWeek().getValue() + 6) % 7; // Mon=0..Sun=6
        int days = current.lengthOfMonth();

        int rowOffset = 1; // data starts from row 1 (row 0 is header)
        for (int d = 1; d <= days; d++) {
            int index = shift + (d - 1);
            int col = index % 7;
            int row = index / 7 + rowOffset;
            LocalDate date = current.atDay(d);
            monthGrid.add(makeDayCell(date), col, row);
        }
    }

    /** Create one calendar day cell for the month grid. */
    private Node makeDayCell(LocalDate date) {
        VBox box = new VBox(4);
        box.getStyleClass().add("day-cell");
        box.setFillWidth(true);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);    // allow stretching
        GridPane.setHgrow(box, Priority.ALWAYS);
        GridPane.setVgrow(box, Priority.ALWAYS);

        Label title = new Label(Integer.toString(date.getDayOfMonth()));
        title.getStyleClass().add("day-num");

        VBox eventBox = new VBox(4);             // container for event pills
        eventBox.getStyleClass().add("event-box");

        box.getChildren().addAll(title, eventBox);

        // Fill events when rendering for the first time
        fillEventPills(eventBox, date);

        box.setOnMouseClicked(e -> {
            selectDay(date);
            // Refresh right-side “day details” list
            refreshDayDetails();
        });

        if (date.equals(selected)) {
            box.getStyleClass().add("selected");
        }
        return box;
    }

    /** Select a specific day in the calendar. */
    private void selectDay(LocalDate date) {
        selected = date;
        refreshDayDetails();        // refresh when user clicks a day
        renderMonth();
    }

    /** Refresh the “day details” list on the right. */
    private void refreshDayDetails() {
        List<String> display = getEventsFor(selected).stream()
                .map(e -> e.title)
                .toList();
        dayEvents.getItems().setAll(display);
    }

    // ========= Lightweight demo event model (can be replaced by real Event later) =========
    static class EventItem {
        final String title;      // text to display (may include time / course)
        final String category;   // used for color styling ("course", "exam", "life"...)

        EventItem(String title, String category) {
            this.title = title;
            this.category = category;
        }
    }

    /** Demo events for a given date (fake data for now). */
    private List<EventItem> getEventsFor(LocalDate date) {
        // simple demo data
        if (date.getDayOfMonth() % 7 == 0) {
            return List.of(
                    new EventItem("CSC207H • 14:00", "course"),
                    new EventItem("MUS207H • 16:00", "course"),
                    new EventItem("Gym • 19:00", "life"),
                    new EventItem("…", "life")
            );
        }
        if (date.equals(LocalDate.now())) {
            return List.of(
                    new EventItem("Standup 09:00", "work"),
                    new EventItem("Lunch 13:30", "life"),
                    new EventItem("Gym 19:00", "life")
            );
        }
        return List.of();
    }

    /** Create a single “event pill” label for one event. */
    private Label makePill(EventItem e) {
        Label pill = new Label(e.title);
        pill.getStyleClass().addAll("event-pill", "pill-" + e.category); // color by category
        pill.setMaxWidth(Double.MAX_VALUE);
        pill.setTextOverrun(OverrunStyle.ELLIPSIS); // show ellipsis if text is too long
        return pill;
    }

    /** Fill the event container for one day with up to 3 pills and a “+n” label. */
    private void fillEventPills(VBox eventBox, LocalDate date) {
        eventBox.getChildren().clear();

        List<EventItem> list = getEventsFor(date);
        int limit = 3;                      // max number of pills per day cell
        int shown = Math.min(limit, list.size());

        for (int i = 0; i < shown; i++) {
            eventBox.getChildren().add(makePill(list.get(i)));
        }
        if (list.size() > limit) {
            int more = list.size() - limit;
            Label moreLbl = new Label("+" + more);
            moreLbl.getStyleClass().add("event-more");
            eventBox.getChildren().add(moreLbl);

            // On hover, show all events in a tooltip
            String all = list.stream()
                    .map(it -> "• " + it.title)
                    .reduce((a, b) -> a + "\n" + b)
                    .orElse("");
            Tooltip.install(eventBox, new Tooltip(all));
        }
    }

    // ================== WEATHER: button callback ==================

    /**
     * Called by the FXML button with onAction="#onWeatherSearchClicked".
     *
     * Flow:
     *   1. Read city / country from TextFields
     *   2. Call weatherService.fetchWeather(city, country)
     *   3. Read display strings from weatherService and update Labels
     */
    @FXML
    private void onWeatherSearchClicked() {
        if (cityField == null || countryField == null) {
            // FXML not wired correctly; avoid NPE in that case
            return;
        }

        String city = cityField.getText();
        String country = countryField.getText();

        // 1) Trigger the core weather use case via the UI service
        weatherService.fetchWeather(city, country);

        // 2) Read results from the UI service / ViewModel
        String error = weatherService.getError();
        if (error != null && !error.isBlank()) {
            // Error case: show only the error text and clear all other labels
            weatherErrorLabel.setText(error);
            weatherLocationLabel.setText("");
            weatherCurrentLabel.setText("");
            weatherRangeLabel.setText("");
            weatherConditionLabel.setText("");
        } else {
            // Success case: clear error and show all weather information
            weatherErrorLabel.setText("");
            weatherLocationLabel.setText(weatherService.getLocationDisplay());
            weatherCurrentLabel.setText(weatherService.getCurrentTempText());
            weatherRangeLabel.setText(weatherService.getTodayRangeText());
            weatherConditionLabel.setText(weatherService.getDescriptionText());
        }
    }
}
