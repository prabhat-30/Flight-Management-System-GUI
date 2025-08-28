package com.example.flightreservationsystemfinal;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class AddFlightController {

    @FXML private TextField flightNumberField;
    @FXML private TextField airlineField;
    @FXML private TextField originField;
    @FXML private TextField destinationField;
    @FXML private DatePicker datePicker;
    @FXML private TextField depTimeField;
    @FXML private TextField arrTimeField;
    @FXML private TextField capacityField;
    @FXML private TextField fareField;

    private ViewFlightsController parentController;

    public void setParentController(ViewFlightsController parentController) {
        this.parentController = parentController;
    }

    @FXML
    void handleSaveButtonAction() {
        try {
            String flightNum = flightNumberField.getText();
            String airlineCode = airlineField.getText().toUpperCase();
            String originCode = originField.getText().toUpperCase();
            String destCode = destinationField.getText().toUpperCase();
            String date = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String depTime = depTimeField.getText();
            String arrTime = arrTimeField.getText();
            int capacity = Integer.parseInt(capacityField.getText());
            double fare = Double.parseDouble(fareField.getText());

            boolean success = Flight.addFlight(flightNum, airlineCode, originCode, destCode,
                    date, depTime, arrTime, capacity, fare);

            if (success) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "New flight added successfully.");
                parentController.loadFlights(); // Refresh the table
                closeWindow();
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Failure", "Could not add flight. Check if airline/airport codes are valid.");
            }
        } catch (NumberFormatException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Invalid Input", "Capacity and Fare must be numbers.");
        } catch (SQLException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "A database error occurred.");
        } catch (NullPointerException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Missing Input", "Please fill out all fields, including the date.");
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