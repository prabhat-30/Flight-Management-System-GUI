package com.example.flightreservationsystemfinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import java.sql.SQLException;
import java.util.List;

public class ViewUsersController {

    @FXML private TableView<User> usersTable;
    @FXML private TextField selectedUserField;
    private final ObservableList<User> userData = FXCollections.observableArrayList();
    private User selectedUser = null;

    @FXML
    public void initialize() {
        usersTable.setItems(userData);
        loadUsers();

        usersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedUser = newSelection;
                selectedUserField.setText(selectedUser.getUsername());
            } else {
                selectedUser = null;
                selectedUserField.clear();
            }
        });
    }

    @FXML
    void loadUsers() {
        try {
            List<User> users = User.getAllUsers();
            userData.setAll(users);
        } catch (SQLException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load user data.");
            e.printStackTrace();
        }
    }

    @FXML
    void handlePromote() {
        if (selectedUser == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to promote.");
            return;
        }
        if (User.promoteUserToAdmin(selectedUser.getUsername())) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Promoted to Admin.");
            loadUsers();
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Failed", "Could not promote. User may already be an admin.");
        }
    }

    @FXML
    void handleDemote() {
        if (selectedUser == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a user to demote.");
            return;
        }
        if (selectedUser.getUsername().equals(SessionManager.getCurrentUser().getUsername())) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Not Allowed", "You cannot demote yourself.");
            return;
        }

        if (User.demoteAdminToUser(selectedUser.getUsername())) {
            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Demoted to Passenger.");
            loadUsers();
        } else {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Failed", "Could not demote. User may not be an admin.");
        }
    }

    @FXML
    void handleRefresh() {
        loadUsers();
    }
}