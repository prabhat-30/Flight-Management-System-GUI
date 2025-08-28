package com.example.flightreservationsystemfinal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class MyReservationsController {

    @FXML private TableView<Reservation> reservationsTable;
    private ObservableList<Reservation> reservationData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        reservationsTable.setItems(reservationData);
        loadReservations();
    }

    private void loadReservations() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) return;

        try {
            List<Reservation> myReservations = Passenger.getMyReservations(currentUser.getId());
            reservationData.setAll(myReservations);
        } catch (SQLException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load reservations.");
            e.printStackTrace();
        }
    }

    @FXML
    void handleViewTicket() {
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to view.");
            return;
        }
        try {
            String ticketContent = Passenger.generateTicketString(selected.getTicketId());
            if (ticketContent != null) {
                showTicketDialog(ticketContent);
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Could not generate ticket.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showTicketDialog(String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("E-Ticket");
        alert.setHeaderText("Your E-Ticket has been generated and saved to a file.");

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setFont(javafx.scene.text.Font.font("monospaced"));

        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);
        alert.getDialogPane().setExpanded(true);
        alert.showAndWait();
    }

    // Add this method inside your MyReservationsController.java class

    @FXML
    void handleCancelTicket() {
        // 1. Get the selected item from the table
        Reservation selected = reservationsTable.getSelectionModel().getSelectedItem();

        // 2. Check if anything was selected
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to cancel.");
            return;
        }

        // 3. Ask for confirmation
        boolean confirmed = AlertHelper.showConfirmation("Confirm Cancellation",
                "Are you sure you want to cancel ticket " + selected.getTicketId() + "?\nThis action cannot be undone.");

        // 4. If confirmed, proceed with cancellation
        if (confirmed) {
            boolean success = Passenger.cancelReservation(selected.getTicketId());

            if (success) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Ticket has been successfully cancelled.");
                loadReservations(); // Refresh the table to show the change
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "Could not cancel the ticket. Please try again.");
            }
        }
    }

    @FXML
    void handleBack() throws IOException {
        Main.changeScene("PassengerDashboard.fxml");
    }
}