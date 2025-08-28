package com.example.flightreservationsystemfinal;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PassengerDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TableView<Flight> flightsTable;
    @FXML private Spinner<Integer> numSeatsSpinner;

    private ObservableList<Flight> flightData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getFirstName() + "!");
        }
        flightsTable.setItems(flightData);
    }

    @FXML
    void handleSearchFlights() {
        String origin = originField.getText().toUpperCase();
        String destination = destinationField.getText().toUpperCase();

        if (origin.isEmpty() || destination.isEmpty() || datePicker.getValue() == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please fill in all fields.");
            return;
        }
        String date = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);

        try {
            List<Flight> apiFlights = FlightApiClient.findFlights(origin, destination, date);
            flightData.setAll(apiFlights);
            if (apiFlights.isEmpty()) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "No Flights", "No flights found for this route and date.");
            }
        } catch (IOException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "API Error", "Could not fetch flight data.");
            e.printStackTrace();
        }
    }

    @FXML
    void handleBookFlight() {
        Flight selectedFlightFromApi = flightsTable.getSelectionModel().getSelectedItem();
        if (selectedFlightFromApi == null) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "No Selection", "Please select a flight to book.");
            return;
        }

        int numSeats = numSeatsSpinner.getValue();
        String date = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
        ApiFlight flightToBook = Passenger.convertToApiFlight(selectedFlightFromApi, date);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SeatSelection.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Select Seats for Flight " + flightToBook.getFlightNumber());
            stage.setScene(new Scene(loader.load()));

            SeatSelectionController controller = loader.getController();
            controller.loadSeatMap(flightToBook, numSeats);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleMyReservations() throws IOException {
        Main.changeScene("MyReservations.fxml");
    }

    @FXML
    void handleLogout() throws IOException {
        SessionManager.clearSession();
        Main.changeScene("Login.fxml");
    }
}