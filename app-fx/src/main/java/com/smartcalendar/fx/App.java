package com.smartcalendar.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // 先加载登录界面 Load Login window first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
        Parent root = loader.load();

        // inject stage into LoginFxController，for changing the window after login
        LoginFxController controller = loader.getController();
        controller.setPrimaryStage(stage);

        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("SmartCalendar - Login");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
