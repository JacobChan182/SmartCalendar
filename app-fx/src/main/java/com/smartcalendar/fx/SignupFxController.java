package com.smartcalendar.fx;

import data_access.SQLiteUserDataAccessObject;
import entity.UserFactory;
import interface_adapter.ViewManagerModel;
import interface_adapter.login.LoginViewModel;
import interface_adapter.signup.SignupController;
import interface_adapter.signup.SignupPresenter;
import interface_adapter.signup.SignupState;
import interface_adapter.signup.SignupViewModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import use_case.signup.SignupInputBoundary;
import use_case.signup.SignupInteractor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

public class SignupFxController implements PropertyChangeListener {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;
    @FXML private Label errorLabel;

    private SignupController signupController;
    private SignupViewModel signupViewModel;
    private LoginViewModel loginViewModel;
    private ViewManagerModel viewManagerModel;

    // Primary stage is injected so we can switch between signup and login scenes
    private Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML
    private void initialize() {
        // === 1. Wire up the Signup use case object graph ===
        UserFactory userFactory = new UserFactory();
        String dbPath = "smartcalendar.db";
        SQLiteUserDataAccessObject userDao =
                new SQLiteUserDataAccessObject(dbPath, userFactory);

        signupViewModel = new SignupViewModel();
        loginViewModel = new LoginViewModel();
        viewManagerModel = new ViewManagerModel();

        // Constructor order: ViewManagerModel, SignupViewModel, LoginViewModel
        SignupPresenter signupPresenter =
                new SignupPresenter(viewManagerModel, signupViewModel, loginViewModel);
        SignupInputBoundary interactor =
                new SignupInteractor(userDao, signupPresenter, userFactory);
        signupController = new SignupController(interactor);

        // === 2. Listen to SignupViewModel for validation errors ===
        signupViewModel.addPropertyChangeListener(this);

        // === 3. Listen to ViewManagerModel; when it switches to "log in",
        //         we go back to the login scene ===
        viewManagerModel.addPropertyChangeListener(evt -> {
            String viewName = viewManagerModel.getState();
            if ("log in".equals(viewName)) {  // view name used by LoginViewModel
                openLoginView();
            }
        });
    }

    @FXML
    private void onSignupClicked() {
        errorLabel.setText("");

        String username = usernameField.getText();
        String pw1 = passwordField.getText();
        String pw2 = repeatPasswordField.getText();

        signupController.execute(username, pw1, pw2);
    }

    @FXML
    private void onBackToLoginClicked() {
        // Trigger the use case to switch back to the login view
        signupController.switchToLoginView();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == signupViewModel) {
            SignupState state = signupViewModel.getState();
            // For now, presenter puts error messages in usernameError
            errorLabel.setText(state.getUsernameError());
        }
    }

    /** Open the login view (switch scene back to login). */
    private void openLoginView() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("LoginView.fxml"));
            Parent root = loader.load();

            LoginFxController loginController = loader.getController();
            loginController.setPrimaryStage(primaryStage);

            Scene scene = new Scene(root, 400, 300);
            primaryStage.setScene(scene);
            primaryStage.setTitle("SmartCalendar - Login");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to open login view.");
        }
    }
}
