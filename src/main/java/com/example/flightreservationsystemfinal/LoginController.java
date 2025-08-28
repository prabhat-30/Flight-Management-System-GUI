package com.example.flightreservationsystemfinal;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    public void handleLoginButtonAction() throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Username and password cannot be empty.");
            return;
        }

        User passenger = User.userLogin(username, password, "passenger");
        if (passenger != null) {
            SessionManager.setCurrentUser(passenger);
            Main.changeScene("PassengerDashboard.fxml");
            return;
        }

        User admin = User.userLogin(username, password, "admin");
        if (admin != null) {
            SessionManager.setCurrentUser(admin);
            Main.changeScene("AdminDashboard.fxml");
            return;
        }

        AlertHelper.showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username, password, or role.");
    }

    public void handleRegisterButtonAction() throws IOException {
        Main.changeScene("Register.fxml");
    }
}