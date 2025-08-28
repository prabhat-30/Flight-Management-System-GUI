package com.example.flightreservationsystemfinal;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;

public class RegisterController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField usernameField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML
    void handleRegisterButtonAction() throws IOException {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String username = usernameField.getText();
        String phone = phoneField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Form Error!", "Please fill in all required fields.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Password Error!", "Passwords do not match.");
            return;
        }

        boolean success = User.registerUser(username, password, firstName, lastName, phone, "passenger");

        if (success) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Registration Successful!", "Account created. Please log in.");
            Main.changeScene("Login.fxml");
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Registration Failed!", "Username '" + username + "' is already taken.");
        }
    }

    @FXML
    void handleBackButtonAction() throws IOException {
        Main.changeScene("Login.fxml");
    }
}