package com.smartcalendar.fx;

import data_access.SQLiteUserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.login.LoginController;
import interface_adapter.login.LoginPresenter;
import interface_adapter.login.LoginState;
import interface_adapter.login.LoginViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import use_case.login.LoginInputBoundary;
import use_case.login.LoginInteractor;

import java.io.IOException;

public class LoginFxController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private LoginController loginController;
    private LoginViewModel loginViewModel;
    private LoggedInViewModel loggedInViewModel;
    private ViewManagerModel viewManagerModel;

    // Primary stage is injected from App so we can switch scenes
    private Stage primaryStage;
    private String currentUsername;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        // === 1. Wire up the Login use case object graph ===
        UserFactory userFactory = new UserFactory();
        // Same DB path as in AppBuilder – SQLite database file
        String dbPath = "smartcalendar.db";
        SQLiteUserDataAccessObject userDao =
                new SQLiteUserDataAccessObject(dbPath, userFactory);

        loginViewModel = new LoginViewModel();
        loggedInViewModel = new LoggedInViewModel();
        viewManagerModel = new ViewManagerModel();

        // Order of arguments: ViewManagerModel, LoggedInViewModel, LoginViewModel
        LoginPresenter loginPresenter =
                new LoginPresenter(viewManagerModel, loggedInViewModel, loginViewModel);
        LoginInputBoundary interactor =
                new LoginInteractor(userDao, loginPresenter);
        loginController = new LoginController(interactor);

        // === 2. Listen to LoginViewModel for validation / login errors ===
        loginViewModel.addPropertyChangeListener(evt -> {
            LoginState state = loginViewModel.getState();
            errorLabel.setText(state.getLoginError());
        });

        // === 3. Listen to ViewManagerModel; when it switches to "logged in",
        //         we actually open the calendar view ===
        viewManagerModel.addPropertyChangeListener(evt -> {
            String viewName = viewManagerModel.getState();
            if ("logged in".equals(viewName)) {
                openCalendarView();
            }
        });
    }

    @FXML
    private void onLoginClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        this.currentUsername = username;
        errorLabel.setText("");
        loginController.execute(username, password);
    }

    @FXML
    private void onSignupClicked() {
        // Open the JavaFX signup view
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("SignupView.fxml"));
            Parent root = loader.load();

            SignupFxController signupController = loader.getController();
            signupController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 400, 340);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SmartCalendar - Sign up");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            if (errorLabel != null) {
                errorLabel.setText("Failed to open signup view.");
            }
        }
    }

    /** Open the main calendar view after a successful login. */
    private void openCalendarView() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("MainView.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setCurrentUser(currentUsername);

            Scene scene = new Scene(root, 1280, 900);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SmartCalendar");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to open calendar view.");
        }
    }
}
