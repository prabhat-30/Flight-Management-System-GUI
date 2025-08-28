package com.example.flightreservationsystemfinal;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;

public class AdminDashboardController {

    @FXML private Label welcomeLabel;

    @FXML
    public void initialize() {
        User admin = SessionManager.getCurrentUser();
        if (admin != null) {
            welcomeLabel.setText("Admin Panel | Logged in as: " + admin.getUsername());
        }
    }

    @FXML
    void handleLogout() throws IOException {
        SessionManager.clearSession();
        Main.changeScene("Login.fxml");
    }
}