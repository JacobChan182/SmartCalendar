package com.smartcalendar.fx;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.*;
import java.util.List;

// ===== 天气部分：UI 调用的 service 接口 =====
import weather.CoreWeatherUiService;
import weather.WeatherUiService;

public class MainController {

    // ================== 日历 UI 控件 ==================
    @FXML private Label lblYearMonth;
    @FXML private GridPane monthGrid;
    @FXML private Button btnPrev, btnNext;
    @FXML private ListView<String> dayEvents;

    // 当前显示的月份 & 选中的日期
    private YearMonth current = YearMonth.now();
    private LocalDate selected = LocalDate.now();

    // ================== WEATHER: UI fields & service ==================

    // 用户输入城市和国家（TextField 在 FXML 里要有 fx:id）
    @FXML private TextField cityField;      // e.g. "Toronto"
    @FXML private TextField countryField;   // e.g. "Canada" or "CA"

    // 显示天气结果的标签（对应 FXML 里的 Label fx:id）
    @FXML private Label weatherLocationLabel;    // "Toronto, Canada"
    @FXML private Label weatherCurrentLabel;     // "Current: 3.4 °C"
    @FXML private Label weatherRangeLabel;       // "Today: -1.0 °C ~ 6.2 °C"
    @FXML private Label weatherConditionLabel;   // "Partly cloudy"
    @FXML private Label weatherErrorLabel;       // 出错时显示错误信息

    // UI 访问 core 的入口：内部会连 controller / interactor / gateway
    private final WeatherUiService weatherService = new CoreWeatherUiService();

    // ================== 初始化（由 JavaFX 自动调用） ==================
    @FXML
    private void initialize() {
        // 上一月 & 下一月按钮
        btnPrev.setOnAction(e -> changeMonth(-1));
        btnNext.setOnAction(e -> changeMonth(1));

        // ⭐ 天气：可选，给输入框一个默认值，方便测试
        if (cityField != null && countryField != null) {
            cityField.setText("Toronto");
            countryField.setText("Canada");
        }

        // 渲染当月日历 + 右侧当天事件列表
        renderMonth();
        refreshDayDetails();
    }

    // ================== 日历逻辑 ==================

    /** 改变当前月份（delta 可以是 -1 / +1 等） */
    private void changeMonth(int delta) {
        current = current.plusMonths(delta);
        selected = current.atDay(Math.min(
                selected.getDayOfMonth(), current.lengthOfMonth()));
        renderMonth();
    }

    /** 根据 current 渲染整个月的格子 */
    private void renderMonth() {
        lblYearMonth.setText(current.toString()); // e.g. "2025-11"
        monthGrid.getChildren().clear();

        // title of the table（Mon..Sun）, monday is at the start
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

    /** 创建单个日期的小方块 cell */
    private Node makeDayCell(LocalDate date) {
        VBox box = new VBox(4);
        box.getStyleClass().add("day-cell");
        box.setFillWidth(true);
        box.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);    // 允许拉伸
        GridPane.setHgrow(box, Priority.ALWAYS);
        GridPane.setVgrow(box, Priority.ALWAYS);

        Label title = new Label(Integer.toString(date.getDayOfMonth()));
        title.getStyleClass().add("day-num");

        VBox eventBox = new VBox(4);             // 事件容器
        eventBox.getStyleClass().add("event-box");

        box.getChildren().addAll(title, eventBox);

        // 初次渲染就填充事件
        fillEventPills(eventBox, date);

        box.setOnMouseClicked(e -> {
            selectDay(date);
            // 右侧列表刷新
            refreshDayDetails();
        });

        if (date.equals(selected)) box.getStyleClass().add("selected");
        return box;
    }

    /** 选择某一天 */
    private void selectDay(LocalDate date) {
        selected = date;
        refreshDayDetails();        // 点击时也刷新
        renderMonth();
    }

    /** 更新右侧当天事件列表 */
    private void refreshDayDetails() {
        List<String> display = getEventsFor(selected).stream()
                .map(e -> e.title)
                .toList();
        dayEvents.getItems().setAll(display);
    }

    // ========= demo 用的轻量事件模型（之后可替换成真正 Event 实体） =========
    static class EventItem {
        final String title;      // 展示文本（含时间或科目）
        final String category;   // 用于着色（如 "course", "exam", "life"...）

        EventItem(String title, String category) {
            this.title = title;
            this.category = category;
        }
    }

    /** 取某一天的 demo 事件（假数据） */
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

    /** 创建一个“事件 pill”标签 */
    private Label makePill(EventItem e) {
        Label pill = new Label(e.title);
        pill.getStyleClass().addAll("event-pill", "pill-" + e.category); // 颜色按类别
        pill.setMaxWidth(Double.MAX_VALUE);
        pill.setTextOverrun(OverrunStyle.ELLIPSIS); // 超长省略号
        return pill;
    }

    /** 给一个日期的事件容器填充 2–3 条 pill，并附加 +n */
    private void fillEventPills(VBox eventBox, LocalDate date) {
        eventBox.getChildren().clear();

        List<EventItem> list = getEventsFor(date);
        int limit = 3;                      // 每格最多展示数量
        int shown = Math.min(limit, list.size());

        for (int i = 0; i < shown; i++) {
            eventBox.getChildren().add(makePill(list.get(i)));
        }
        if (list.size() > limit) {
            int more = list.size() - limit;
            Label moreLbl = new Label("+" + more);
            moreLbl.getStyleClass().add("event-more");
            eventBox.getChildren().add(moreLbl);

            // 鼠标悬停 tooltip 展示全部
            String all = list.stream()
                    .map(it -> "• " + it.title)
                    .reduce((a,b)->a+"\n"+b)
                    .orElse("");
            Tooltip.install(eventBox, new Tooltip(all));
        }
    }

    // ================== WEATHER: 按钮回调 ==================

    /**
     * FXML 按钮 onAction="#onWeatherSearchClicked" 会调用这个方法。
     *
     * 流程：
     *   1. 从 TextField 读取 city / country
     *   2. 调用 weatherService.fetchWeather(city, country)
     *   3. 从 weatherService 取出展示文本，更新几个 Label
     */
    @FXML
    private void onWeatherSearchClicked() {
        if (cityField == null || countryField == null) {
            // FXML 没连好，为了防止 NPE，直接返回
            return;
        }

        String city = cityField.getText();
        String country = countryField.getText();

        // 1) 调用 core 层用例（通过 UI service）
        weatherService.fetchWeather(city, country);

        // 2) 从 UI service / ViewModel 读取结果
        String error = weatherService.getError();
        if (error != null && !error.isBlank()) {
            // 有错误：显示错误信息，清空其它 label
            weatherErrorLabel.setText(error);
            weatherLocationLabel.setText("");
            weatherCurrentLabel.setText("");
            weatherRangeLabel.setText("");
            weatherConditionLabel.setText("");
        } else {
            // 成功：清空错误，显示天气信息
            weatherErrorLabel.setText("");
            weatherLocationLabel.setText(weatherService.getLocationDisplay());
            weatherCurrentLabel.setText(weatherService.getCurrentTempText());
            weatherRangeLabel.setText(weatherService.getTodayRangeText());
            weatherConditionLabel.setText(weatherService.getDescriptionText());
        }
    }
}
