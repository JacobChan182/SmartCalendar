package com.smartcalendar.fx;

import data_access.InMemoryEventDataAccessObject;
import entity.Event;

import interface_adapter.addEvent.AddEventPresenter;
import interface_adapter.addEvent.AddEventUserAccess;
import interface_adapter.addEvent.AddEventView;
import interface_adapter.editEvent.EditEventView;
import use_case.addEvent.AddEventInteractor;
import use_case.addEvent.EventMethodsDataAccessInterface;
import interface_adapter.editEvent.EditEventPresenter;
import interface_adapter.editEvent.EditEventUserAccess;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import use_case.editEvents.EditEventInteractor;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainController implements AddEventView, EditEventView {
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
    @FXML private TextField reminderField;



    private YearMonth current = YearMonth.now();
    private LocalDate selected = LocalDate.now();

    private final AddEventUserAccess addEventUserAccess;
    private final EditEventUserAccess editEventUserAccess;

    private final EventMethodsDataAccessInterface repository;

    public MainController() {
        this.repository = new InMemoryEventDataAccessObject();

        //Add events setup
        AddEventPresenter addEventPresenter = new AddEventPresenter(this);
        AddEventInteractor addEventInteractor = new AddEventInteractor(repository, addEventPresenter);
        this.addEventUserAccess = new AddEventUserAccess(addEventInteractor);

        //Edit events setup
        EditEventPresenter editEventPresenter = new EditEventPresenter(this);
        EditEventInteractor editEventInteractor = new EditEventInteractor(repository, editEventPresenter);
        this.editEventUserAccess = new EditEventUserAccess(editEventInteractor);
    }
    @FXML
    private void initialize() {
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
        renderMonth();
        refreshDayDetails();
    }

    private void changeMonth(int delta) {
        current = current.plusMonths(delta);
        selected = current.atDay(Math.min(
                selected.getDayOfMonth(), current.lengthOfMonth()));
        renderMonth();
    }

    private void renderMonth() {
        lblYearMonth.setText(current.toString()); // e.g. 2025-11
        monthGrid.getChildren().clear();

        // title of the table（Mon..Sun）, monday is at the start, can
        var headers = List.of("Mon","Tue","Wed","Thu","Fri","Sat","Sun");
        for (int c = 0; c < 7; c++) {
            var hdr = new Label(headers.get(c));
            hdr.getStyleClass().add("weekday");
            monthGrid.add(hdr, c, 0);
        }

        LocalDate first = current.atDay(1);
        int shift = (first.getDayOfWeek().getValue() + 6) % 7; // Mon=0..Sun=6
        int days = current.lengthOfMonth();

        int rowOffset = 1; // starts at line 0
        for (int d = 1; d <= days; d++) {
            int index = shift + (d - 1);
            int col = index % 7;
            int row = index / 7 + rowOffset;
            LocalDate date = current.atDay(d);
            monthGrid.add(makeDayCell(date), col, row);
        }
    }

    //Add Events methods
    @Override
    public void showAddedEvent(Event event) {
        refreshDayDetails();
        renderMonth();
    }

    public void addEvent() {
        try {
            Event event = new Event (
                    UUID.randomUUID(),
                    titleField.getText(),
                    LocalDateTime.of(datePicker.getValue(), LocalTime.parse(startField.getText())),
                    LocalDateTime.of(datePicker.getValue(), LocalTime.parse(endField.getText())),
                    locationField.getText(),
                    categoryCombo.getValue(),
                    reminderField.getText()
            );
            addEventUserAccess.addEvent(event);
        } catch (Exception exception) {
            showError("Failed to add event: " + exception.getMessage());
        }
    }

    @Override
    public void showError(String message) {
        System.err.println(message);
    }

    @Override
    public void showEditedEvent(Event event) {
        refreshDayDetails();
        renderMonth();
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
                        event.getReminderMessage()
                );
                editEventUserAccess.editEvent(
                        updated.getId(),
                        updated.getTitle(),
                        updated.getStart(),
                        updated.getEnd(),
                        updated.getLocation(),
                        updated.getCategory(),
                        updated.getReminderMessage()
                );
            });
        }
    }




    private Node makeDayCell(LocalDate date) {
        VBox box = new VBox(4);
        box.getStyleClass().add("day-cell");
        box.setFillWidth(true);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);    // CH：关键：允许拉伸 EN: Key feature: allows stretching
        GridPane.setHgrow(box, Priority.ALWAYS);
        GridPane.setVgrow(box, Priority.ALWAYS);

        Label title = new Label(Integer.toString(date.getDayOfMonth()));
        title.getStyleClass().add("day-num");

        VBox eventBox = new VBox(4);             // CN: 事件容器 EN: Event container
        eventBox.getStyleClass().add("event-box");

        box.getChildren().addAll(title, eventBox);

        // CN: 初次渲染就填充事件 EN: load and render the events at the beginning
        fillEventPills(eventBox, date);

        box.setOnMouseClicked(e -> {
            selectDay(date);
            // CN: 右侧列表刷新 EN: right side list refresh
            refreshDayDetails();
        });

        if (date.equals(selected)) box.getStyleClass().add("selected");
        return box;
    }

    private void selectDay(LocalDate date) {
        selected = date;
        refreshDayDetails();        // CN: 点击时也刷新 EN: refresh when clicking
        renderMonth();
    }
    private void refreshDayDetails() {
        List<String> display = new ArrayList<>();
        for (Event e : getEventsFor(selected)) {
            display.add(e.getTitle());
        }
        dayEvents.getItems().setAll(getEventsFor(selected));
    //TODO CN: 轻量事件模型（你接 core 后可替换为真实 Event） EN: Event interface, replace it when core is done

    }
    //TODO CN: 取当天事件（先用假数据；接 core 后替换）EN: Get events for the specific day, replace when Event class is ready
    private List<Event> getEventsFor(LocalDate date) {
        return repository.getEventsForDay(date);
    }
    // CN: 创建一个“事件 pill”标签 EN: make a "eventpill" for those blocks
    private Label makePill(Event e) {
        Label pill = new Label(e.getTitle());
        pill.getStyleClass().addAll("event-pill", "pill-" + e.getCategory().name().toLowerCase()); // CN：颜色按类别 EN：colors-category
        pill.setMaxWidth(Double.MAX_VALUE);
        pill.setTextOverrun(OverrunStyle.ELLIPSIS); // CN: 超长省略号 EN: Extra-long ellipsis
        return pill;
    }
    // CN: 给一个日期的事件容器填充 2–3 条 pill，并附加 +n EN: each day block will have 2-3 pills, rest shows as +n
    private void fillEventPills(VBox eventBox, LocalDate date) {
        eventBox.getChildren().clear();

        List<Event> list = getEventsFor(date);
        int limit = 3;                      // CN: 每格最多展示数量 EN: Maximum number of pills in each block
        int shown = Math.min(limit, list.size());

        for (int i = 0; i < shown; i++) {
            eventBox.getChildren().add(makePill(list.get(i)));
        }
        if (list.size() > limit) {
            int more = list.size() - limit;
            Label moreLbl = new Label("+" + more);
            moreLbl.getStyleClass().add("event-more");
            eventBox.getChildren().add(moreLbl);

            // CN：鼠标悬停 tooltip 展示全部 EN：mouse hover to show all info
            String all = list.stream().map(it -> "• " + it.getTitle()).reduce((a,b)->a+"\n"+b).orElse("");
            Tooltip.install(eventBox, new Tooltip(all));
        }
    }
}
