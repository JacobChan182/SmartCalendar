package com.smartcalendar.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("SmartCalendar (JavaFX)");
        stage.setScene(new Scene(new Label("Hello, JavaFX!"), 900, 650));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
