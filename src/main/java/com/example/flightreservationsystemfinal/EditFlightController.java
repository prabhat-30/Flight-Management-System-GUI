package com.example.flightreservationsystemfinal;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditFlightController {

    @FXML private TextField flightNumberField;
    @FXML private TextField airlineField;
    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField depTimeField;
    @FXML private TextField arrTimeField;
    @FXML private TextField capacityField;
    @FXML private TextField fareField;
    @FXML private CheckBox availableCheckBox;

    private Flight flightToEdit;
    private ViewFlightsController parentController;

    public void setParentController(ViewFlightsController parentController) {
        this.parentController = parentController;
    }

    // Pre-fills all fields with the existing flight's data
    public void setFlightData(Flight flight) {
        this.flightToEdit = flight;

        flightNumberField.setText(flight.getFlightNumber());
        // For simplicity, IATA codes must be re-entered if changed.
        // A more advanced version would look up the code from the flight's airline/airport name.
        originField.setText(flight.getOriginCity());
        destinationField.setText(flight.getDestinationCity());
        datePicker.setValue(LocalDate.parse(flight.getDepartureDate()));
        depTimeField.setText(flight.getDepartureTime());
        arrTimeField.setText(flight.getArrivalTime());
        capacityField.setText(String.valueOf(flight.getCapacity()));
        fareField.setText(String.valueOf(flight.getFare()));
        availableCheckBox.setSelected(flight.isAvailable());
    }

    @FXML
    void handleSaveButtonAction() {
        try {
            // Get all values from the form
            String airlineCode = airlineField.getText().toUpperCase();
            String originCode = originField.getText().toUpperCase();
            String destCode = destinationField.getText().toUpperCase();
            String date = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String depTime = depTimeField.getText();
            String arrTime = arrTimeField.getText();
            int capacity = Integer.parseInt(capacityField.getText());
            double fare = Double.parseDouble(fareField.getText());
            boolean isAvailable = availableCheckBox.isSelected();

            // Call the updated backend method
            boolean success = Flight.editFlight(flightToEdit.getId(), airlineCode, originCode, destCode, date,
                    depTime, arrTime, capacity, fare, isAvailable);
            if (success) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Flight details updated successfully.");
                parentController.loadFlights();
                closeWindow();
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Failure", "Could not update flight details. Check IATA codes.");
            }
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Capacity and Fare must be numbers.");
        } catch (SQLException | NullPointerException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Error", "An error occurred. Please check all fields.");
            e.printStackTrace();
        }
    }

    @FXML
    void handleCancelButtonAction() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) flightNumberField.getScene().getWindow();
        stage.close();
    }
}