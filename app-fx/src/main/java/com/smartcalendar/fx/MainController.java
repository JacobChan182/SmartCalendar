package com.smartcalendar.fx;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.*;
import java.util.List;

public class MainController {
    @FXML private Label lblYearMonth;
    @FXML private GridPane monthGrid;
    @FXML private Button btnPrev, btnNext;
    @FXML private ListView<String> dayEvents;

    private YearMonth current = YearMonth.now();
    private LocalDate selected = LocalDate.now();

    @FXML
    private void initialize() {
        btnPrev.setOnAction(e -> changeMonth(-1));
        btnNext.setOnAction(e -> changeMonth(1));
//        System.out.println("[UI] MainController.initialize() called");
//        if (lblYearMonth != null) {
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
        List<String> display = getEventsFor(selected).stream()
                .map(e -> e.title)
                .toList();
        dayEvents.getItems().setAll(display);
    }
    //TODO CN: 轻量事件模型（你接 core 后可替换为真实 Event） EN: Event interface, replace it when core is done
    static class EventItem {
        final String title;      // CN: 展示文本（含时间或科目）EN: title for showing
        final String category;   // CN:用于着色（如 "course", "exam", "life"...） EN: Category for giving colors
        EventItem(String title, String category) {
            this.title = title; this.category = category;
        }
    }
    //TODO CN: 取当天事件（先用假数据；接 core 后替换）EN: Get events for the specific day, replace when Event class is ready
    private List<EventItem> getEventsFor(LocalDate date) {
        // demo：some test data
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
    // CN: 创建一个“事件 pill”标签 EN: make a "eventpill" for those blocks
    private Label makePill(EventItem e) {
        Label pill = new Label(e.title);
        pill.getStyleClass().addAll("event-pill", "pill-" + e.category); // CN：颜色按类别 EN：colors-category
        pill.setMaxWidth(Double.MAX_VALUE);
        pill.setTextOverrun(OverrunStyle.ELLIPSIS); // CN: 超长省略号 EN: Extra-long ellipsis
        return pill;
    }
    // CN: 给一个日期的事件容器填充 2–3 条 pill，并附加 +n EN: each day block will have 2-3 pills, rest shows as +n
    private void fillEventPills(VBox eventBox, LocalDate date) {
        eventBox.getChildren().clear();

        List<EventItem> list = getEventsFor(date);
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
            String all = list.stream().map(it -> "• " + it.title).reduce((a,b)->a+"\n"+b).orElse("");
            Tooltip.install(eventBox, new Tooltip(all));
        }
    }
}
