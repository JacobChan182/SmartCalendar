package com.smartcalendar.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 1) load FXML
        //    FXML physical path should be：app-fx/src/main/resources/com/smartcalendar/fx/MainView.fxml
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainView.fxml"));
        Parent root = loader.load();

        // 2) set Scene/Stage
        Scene scene = new Scene(root, 1000, 700);
        stage.setTitle("SmartCalendar");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
