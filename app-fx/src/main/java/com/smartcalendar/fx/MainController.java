package com.smartcalendar.fx;

import entities.Event;

import data_access.ColorApiDataAccessObject;
import interface_adapter.color_scheme.ColorSchemeController;
import interface_adapter.color_scheme.ColorSchemePresenter;
import interface_adapter.color_scheme.ColorSchemeState;
import interface_adapter.color_scheme.ColorSchemeViewModel;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import use_case.event_changes.EventEdit;

import javafx.scene.paint.Color;
import use_case.get_color_scheme.GetColorSchemeInteractor;
import use_case.get_color_scheme.GetColorSchemeUserDataAccessInterface;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import weather.CoreWeatherUiService;
import weather.WeatherUiService;

public class MainController implements PropertyChangeListener {

    // ================== Calendar UI controls ==================
    @FXML private Label lblYearMonth;
    @FXML private GridPane monthGrid;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private ListView<Event> dayEvents;
    @FXML private ComboBox<Event.CategoryType> categoryCombo;
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextField startField;
    @FXML private TextField endField;
    @FXML private DatePicker datePicker;



    // Color scheme UI components
    @FXML private ColorPicker colorPicker;
    @FXML private Label colorErrorLabel;
    @FXML private VBox colorSchemesContainer;
    @FXML private HBox monochromaticColors;
    @FXML private HBox analogousColors;
    @FXML private HBox complementaryColors;
    @FXML private HBox neutralColors;

    // Current displayed month & selected date
    private YearMonth current = YearMonth.now();
    private LocalDate selected = LocalDate.now();

    private final EventEdit eventEdit = new EventEdit();
    // Color scheme components
    private ColorSchemeViewModel colorSchemeViewModel;
    private ColorSchemeController colorSchemeController;

    // ================== WEATHER: UI fields & service ==================

    // User input for city and country (TextFields must have fx:id in FXML)
    @FXML private TextField cityField;      // e.g. "Toronto"
    @FXML private TextField countryField;   // e.g. "Canada" or "CA"

    // Labels to display weather results (must match fx:id in FXML)
    @FXML private Label weatherLocationLabel;    // "Toronto, Canada"
    @FXML private Label weatherCurrentLabel;     // "Current: 3.4 °C"
    @FXML private Label weatherRangeLabel;       // "Today: -1.0 °C ~ 6.2 °C"
    @FXML private Label weatherConditionLabel;   // "Partly cloudy"
    @FXML private Label weatherErrorLabel;       // Error message, if any

    // Entry point for UI to access the core weather use case
    private final WeatherUiService weatherService = new CoreWeatherUiService();

    // ================== Initialization (called automatically by JavaFX) ==================
    @FXML
    private void initialize() {
        // Previous / next month buttons
        btnPrev.setOnAction(e -> changeMonth(-1));
        btnNext.setOnAction(e -> changeMonth(1));
        for (Event.CategoryType type : Event.CategoryType.values()) {
            categoryCombo.getItems().add(type);
        }

        dayEvents.getSelectionModel().selectedItemProperty()
                .addListener((observant, old, eventselect) -> {
                    if (eventselect != null) {
                        titleField.setText(eventselect.getTitle());
                        locationField.setText(eventselect.getLocation());
                        datePicker.setValue(eventselect.getStart().toLocalDate());
                        startField.setText(eventselect.getStart().toLocalTime().toString());
                        endField.setText(eventselect.getEnd().toLocalTime().toString());
                        categoryCombo.setValue(eventselect.getCategory());
                    }
                } );

        ;//        if (lblYearMonth != null) {
//            lblYearMonth.setText("UI OK  (loaded via FXML)");
//        }

        // Optional: default values for weather search to make testing easier
        if (cityField != null && countryField != null) {
            cityField.setText("Toronto");
            countryField.setText("Canada");
        }

        // Render current month and populate the event list for the selected day
        renderMonth();
        refreshDayDetails();

        // Initialize color scheme feature
        initializeColorScheme();
    }

    private void initializeColorScheme() {
        // Create ViewModel
        colorSchemeViewModel = new ColorSchemeViewModel();
        colorSchemeViewModel.addPropertyChangeListener(this);

        // Create Data Access Object
        GetColorSchemeUserDataAccessInterface colorApiDataAccessObject = new ColorApiDataAccessObject();

        // Create Presenter
        ColorSchemePresenter colorSchemePresenter = new ColorSchemePresenter(colorSchemeViewModel);

        // Create Interactor
        GetColorSchemeInteractor getColorSchemeInteractor = new GetColorSchemeInteractor(
                colorApiDataAccessObject,
                colorSchemePresenter
        );

        // Create Controller
        colorSchemeController = new ColorSchemeController(getColorSchemeInteractor);

        // Initially hide error label
        colorErrorLabel.setVisible(false);
        
        // Set initial color for the color picker (optional - can be removed if you want it to start empty)
        colorPicker.setValue(Color.WHITE);
    }

    /**
     * Called when the user picks a color from the color picker.
     * Automatically fetches color schemes for the selected color.
     */
    @FXML
    private void onColorPicked() {
        Color selectedColor = colorPicker.getValue();
        if (selectedColor == null) {
            return;
        }
        
        // Convert Color to hex string (without #)
        String hexColor = colorToHex(selectedColor);
        
        colorErrorLabel.setVisible(false);
        colorSchemeController.execute(hexColor);
    }
    
    /**
     * Converts a JavaFX Color to a hex string (without #).
     * @param color the JavaFX Color object
     * @return hex string in format "RRGGBB" (uppercase, no #)
     */
    private String colorToHex(Color color) {
        int r = (int) Math.round(color.getRed() * 255);
        int g = (int) Math.round(color.getGreen() * 255);
        int b = (int) Math.round(color.getBlue() * 255);
        return String.format("%02X%02X%02X", r, g, b);
    }

    private void showError(String message) {
        colorErrorLabel.setText(message);
        colorErrorLabel.setVisible(true);
    }

    private void displayColors(List<String> colors, HBox container) {
        container.getChildren().clear();
        for (String hex : colors) {
            Region colorBox = createColorBox(hex);
            container.getChildren().add(colorBox);
        }
    }

    private Region createColorBox(String hex) {
        Region colorBox = new Region();
        colorBox.setPrefSize(40, 40);
        colorBox.setMinSize(40, 40);
        colorBox.setMaxSize(40, 40);
        colorBox.setStyle(String.format(
                "-fx-background-color: #%s; -fx-background-radius: 4; -fx-border-color: #cccccc; -fx-border-radius: 4;",
                hex
        ));

        // Add tooltip with hex code
        Tooltip tooltip = new Tooltip("#" + hex);
        Tooltip.install(colorBox, tooltip);

        return colorBox;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == colorSchemeViewModel) {
            ColorSchemeState state = colorSchemeViewModel.getState();

            if (state.getErrorMessage() != null) {
                showError(state.getErrorMessage());
                // Clear all color displays
                monochromaticColors.getChildren().clear();
                analogousColors.getChildren().clear();
                complementaryColors.getChildren().clear();
                neutralColors.getChildren().clear();
            } else {
                // Display all color schemes
                displayColors(state.getMonochromaticColors(), monochromaticColors);
                displayColors(state.getAnalogousColors(), analogousColors);
                displayColors(state.getComplementaryColors(), complementaryColors);
                displayColors(state.getNeutralColors(), neutralColors);
                colorErrorLabel.setVisible(false);
            }
        }
    }

    // ================== Calendar logic ==================

    /** Change the current month (delta can be -1 / +1 / etc.). */
    private void changeMonth(int delta) {
        current = current.plusMonths(delta);
        selected = current.atDay(Math.min(
                selected.getDayOfMonth(), current.lengthOfMonth()));
        renderMonth();
    }

    /** Render the entire month based on the current YearMonth. */
    private void renderMonth() {
        lblYearMonth.setText(current.toString()); // e.g. "2025-11"
        monthGrid.getChildren().clear();

        // Table header (Mon..Sun), Monday at the start
        var headers = List.of("Mon","Tue","Wed","Thu","Fri","Sat","Sun");
        for (int c = 0; c < 7; c++) {
            var hdr = new Label(headers.get(c));
            hdr.getStyleClass().add("weekday");
            monthGrid.add(hdr, c, 0);
        }

        LocalDate first = current.atDay(1);
        int shift = (first.getDayOfWeek().getValue() + 6) % 7; // Mon=0..Sun=6
        int days = current.lengthOfMonth();

        int rowOffset = 1; // header row is at index 0
        for (int d = 1; d <= days; d++) {
            int index = shift + (d - 1);
            int col = index % 7;
            int row = index / 7 + rowOffset;
            LocalDate date = current.atDay(d);
            monthGrid.add(makeDayCell(date), col, row);
        }
    }


    public void addEvent() {
        try {
            String title = titleField.getText();
            String location = locationField.getText();
            LocalDate date = datePicker.getValue();
            LocalTime start = LocalTime.parse(startField.getText());
            LocalTime end = LocalTime.parse(endField.getText());
            Event.CategoryType category = categoryCombo.getValue();

            Event event = new Event(
                    UUID.randomUUID(),
                    title,
                    LocalDateTime.of(date, start),
                    LocalDateTime.of(date, end),
                    location,
                    category,
                    null
            );

            eventEdit.addEvent(event);
            refreshDayDetails();
            renderMonth();
        } catch (Exception ex) {System.err.println("Failed to add event" + ex.getMessage());}
    }

    public void editEvent() {
        Event event = dayEvents.getSelectionModel().getSelectedItem();
        if (event != null) {
            TextInputDialog dialog = new TextInputDialog(event.getTitle());
            dialog.setHeaderText("Edit");
            dialog.setContentText("Title");
            dialog.showAndWait().ifPresent(newTitle -> {
                Event updated = new Event(
                        event.getId(),
                        newTitle,
                        event.getStart(),
                        event.getEnd(),
                        event.getLocation(),
                        event.getCategory(),
                        event.getReminder()
                );
                eventEdit.editEvent(event.getId(), updated);
                refreshDayDetails();
                renderMonth();
            });
        }
    }

    public void deleteEvent() {
        Event event = dayEvents.getSelectionModel().getSelectedItem();
        if (event != null) {
            eventEdit.deleteEvent(event.getId());
            refreshDayDetails();;
            renderMonth();
        }
    }

    public void updateEvent() {
        Event eventSelect = dayEvents.getSelectionModel().getSelectedItem();
        if (eventSelect != null) {
            Event updated = new Event(
                    eventSelect.getId(),
                    titleField.getText(),
                    LocalDateTime.of(datePicker.getValue(), LocalTime.parse(startField.getText())),
                    LocalDateTime.of(datePicker.getValue(), LocalTime.parse(endField.getText())),
                    locationField.getText(),
                    categoryCombo.getValue(),
                    eventSelect.getReminder()
            );
            eventEdit.editEvent(eventSelect.getId(), updated);
            refreshDayDetails();
            renderMonth();
        }
    }

    /** Create a single day cell. */
    private Node makeDayCell(LocalDate date) {
        VBox box = new VBox(4);
        box.getStyleClass().add("day-cell");
        box.setFillWidth(true);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);    // allow stretching
        GridPane.setHgrow(box, Priority.ALWAYS);
        GridPane.setVgrow(box, Priority.ALWAYS);

        Label title = new Label(Integer.toString(date.getDayOfMonth()));
        title.getStyleClass().add("day-num");

        VBox eventBox = new VBox(4);             // container for small event pills
        eventBox.getStyleClass().add("event-box");

        box.getChildren().addAll(title, eventBox);

        // Fill event pills when first rendering
        fillEventPills(eventBox, date);

        box.setOnMouseClicked(e -> {
            selectDay(date);
            // refresh right-side list
            refreshDayDetails();
        });

        if (date.equals(selected)) box.getStyleClass().add("selected");
        return box;
    }

    /** Select a specific day. */
    private void selectDay(LocalDate date) {
        selected = date;
        refreshDayDetails();        // also refresh details on click
        renderMonth();
    }

    /** Update the event list for the selected day. */
    private void refreshDayDetails() {
        List<String> display = getEventsFor(selected).stream()
                .map(e -> e.title)
                .toList();
        dayEvents.getItems().setAll(display);
    }

    // ========= Lightweight demo event model (can be replaced with real Event entity later) =========
    static class EventItem {
        final String title;      // Text shown for the event (may include time)
        final String category;   // Category for styling (e.g., "course", "exam", "life"...)

        EventItem(String title, String category) {
            this.title = title;
            this.category = category;
        }
        dayEvents.getItems().setAll(getEventsFor(selected));
    

    }

    /** Get demo events for a given day (dummy data). */
    private List<EventItem> getEventsFor(LocalDate date) {
        // Some example test data
        if (date.getDayOfMonth() % 7 == 0)
            return List.of(new EventItem("CSC207H • 14:00", "course"),
                    new EventItem("MUS207H • 16:00", "course"),
                    new EventItem("Gym • 19:00", "life"),
                    new EventItem("…", "life"));
        if (date.equals(LocalDate.now()))
            return List.of(new EventItem("Standup 09:00", "work"),
                    new EventItem("Lunch 13:30", "life"),
                    new EventItem("Gym 19:00", "life"));
        return List.of();
    }

    /** Create a single event “pill” label. */
    private Label makePill(EventItem e) {
        Label pill = new Label(e.title);
        pill.getStyleClass().addAll("event-pill", "pill-" + e.category); // color depends on category
        pill.setMaxWidth(Double.MAX_VALUE);
        pill.setTextOverrun(OverrunStyle.ELLIPSIS); // ellipsis for long text
        return pill;
    }

    /** Fill a day's event container with up to 2–3 pills, plus a "+n" label if there are more. */
    private void fillEventPills(VBox eventBox, LocalDate date) {
        eventBox.getChildren().clear();

        List<EventItem> list = getEventsFor(date);
        int limit = 3;                      // max number of pills shown in a cell
        int shown = Math.min(limit, list.size());

        for (int i = 0; i < shown; i++) {
            eventBox.getChildren().add(makePill(list.get(i)));
        }
        if (list.size() > limit) {
            int more = list.size() - limit;
            Label moreLbl = new Label("+" + more);
            moreLbl.getStyleClass().add("event-more");
            eventBox.getChildren().add(moreLbl);

            // Tooltip with full list of events on hover
            String all = list.stream()
                    .map(it -> "• " + it.title)
                    .reduce((a,b)->a+"\n"+b)
                    .orElse("");
            Tooltip.install(eventBox, new Tooltip(all));
        }
    }

    // ================== WEATHER: button callback ==================

    /**
     * This method is called by the FXML button with onAction="#onWeatherSearchClicked".
     *
     * Flow:
     *   1. Read city and country from the TextFields
     *   2. Call weatherService.fetchWeather(city, country)
     *   3. Read display strings from the service and update the labels
     */
    @FXML
    private void onWeatherSearchClicked() {
        if (cityField == null || countryField == null) {
            // FXML not wired correctly; avoid NPE and return early
            return;
        }

        String city = cityField.getText();
        String country = countryField.getText();

        // 1) Invoke the core weather use case via the UI service
        weatherService.fetchWeather(city, country);

        // 2) Read the result from the UI service / ViewModel
        String error = weatherService.getError();
        if (error != null && !error.isBlank()) {
            // Error: show the error message and clear the other labels
            weatherErrorLabel.setText(error);
            weatherLocationLabel.setText("");
            weatherCurrentLabel.setText("");
            weatherRangeLabel.setText("");
            weatherConditionLabel.setText("");
        } else {
            // Success: clear any error and show weather information
            weatherErrorLabel.setText("");
            weatherLocationLabel.setText(weatherService.getLocationDisplay());
            weatherCurrentLabel.setText(weatherService.getCurrentTempText());
            weatherRangeLabel.setText(weatherService.getTodayRangeText());
            weatherConditionLabel.setText(weatherService.getDescriptionText());
        }
    }
}
