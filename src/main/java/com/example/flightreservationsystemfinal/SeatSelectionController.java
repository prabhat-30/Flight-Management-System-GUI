package com.example.flightreservationsystemfinal;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

public class SeatSelectionController {

    @FXML private Label infoLabel;
    @FXML private GridPane seatGridPane;
    @FXML private Button confirmButton;

    private ApiFlight flightToBook;
    private int numSeatsToSelect;
    private List<ToggleButton> selectedSeats = new ArrayList<>();
    private FlightBookingService bookingService = new FlightBookingService(Database.getConnection());

    public void loadSeatMap(ApiFlight flight, int numSeats) {
        this.flightToBook = flight;
        this.numSeatsToSelect = numSeats;
        infoLabel.setText("Please select " + numSeats + " seat(s).");

        try {
            int flightId = bookingService.getOrCreateFlight(flightToBook);
            SeatManager seatManager = new SeatManager();
            Set<String> bookedSeats = seatManager.getBookedSeats(flightId);
            int capacity = Flight.getFlightById(flightId).getCapacity();

            populateGrid(capacity, bookedSeats);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load seat map.");
        }
    }

    private void populateGrid(int capacity, Set<String> bookedSeats) {
        int rows = (int) Math.ceil(capacity / 6.0);
        char[] seatLetters = {'A', 'B', 'C', ' ', 'D', 'E', 'F'};

        for (int row = 1; row <= rows; row++) {
            for (int col = 0; col < seatLetters.length; col++) {
                if (seatLetters[col] == ' ') {
                    continue; // Aisle
                }
                String seatId = row + "" + seatLetters[col];
                ToggleButton seatButton = new ToggleButton(seatId);
                seatButton.setPrefWidth(50);

                if (bookedSeats.contains(seatId.toUpperCase())) {
                    seatButton.setDisable(true);
                    seatButton.setStyle("-fx-background-color: #ff6347;");
                }

                seatButton.setOnAction(e -> handleSeatSelection(seatButton));
                seatGridPane.add(seatButton, col, row - 1);
            }
        }
    }

    private void handleSeatSelection(ToggleButton seatButton) {
        if (seatButton.isSelected()) {
            if (selectedSeats.size() < numSeatsToSelect) {
                selectedSeats.add(seatButton);
            } else {
                seatButton.setSelected(false); // Deselect if max is reached
            }
        } else {
            selectedSeats.remove(seatButton);
        }
        updateUiState();
    }

    private void updateUiState() {
        infoLabel.setText("Selected " + selectedSeats.size() + " of " + numSeatsToSelect + " seats.");
        confirmButton.setDisable(selectedSeats.size() != numSeatsToSelect);
    }

    @FXML
    void handleConfirm() {
        StringJoiner sj = new StringJoiner(", ");
        for (ToggleButton tb : selectedSeats) {
            sj.add(tb.getText());
        }
        String seatNumbers = sj.toString();

        try {
            int flightId = bookingService.getOrCreateFlight(flightToBook);
            User currentUser = SessionManager.getCurrentUser();
            String ticketId = bookingService.createReservation(currentUser.getId(), flightId, numSeatsToSelect, seatNumbers);

            AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Booking Successful", "Your booking is complete!\nTicket ID: " + ticketId);
            closeWindow();
            Main.changeScene("MyReservations.fxml");

        } catch (SQLException e) {
            e.printStackTrace();
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Booking Failed", "A database error occurred.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}