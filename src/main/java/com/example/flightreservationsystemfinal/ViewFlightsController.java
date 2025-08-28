package com.example.flightreservationsystemfinal;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ViewFlightsController {

    @FXML private TableView<Flight> flightsTable;
    private final ObservableList<Flight> flightData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        flightsTable.setItems(flightData);
        loadFlights();
    }

    @FXML
    void loadFlights() {
        try {
            List<Flight> flights = Flight.getAvailableFlights();
            flightData.setAll(flights);
        } catch (SQLException e) {
            AlertHelper.showAlert(Alert.AlertType.ERROR, "Database Error", "Could not load flight data.");
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddFlight() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddFlight.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Add New Flight");
            stage.setScene(new Scene(loader.load()));

            AddFlightController controller = loader.getController();
            controller.setParentController(this);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleEditFlight() {
        Flight selected = flightsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a flight to edit.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("EditFlight.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Flight");
            stage.setScene(new Scene(loader.load()));

            EditFlightController controller = loader.getController();
            controller.setParentController(this);
            controller.setFlightData(selected);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleRemoveFlight() {
        Flight selected = flightsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertHelper.showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a flight to remove.");
            return;
        }

        boolean confirmed = AlertHelper.showConfirmation("Confirm Deletion", "Are you sure you want to delete flight " + selected.getFlightNumber() + "?");
        if (confirmed) {
            if (Flight.removeFlight(selected.getId())) {
                AlertHelper.showAlert(Alert.AlertType.INFORMATION, "Success", "Flight removed successfully.");
                loadFlights();
            } else {
                AlertHelper.showAlert(Alert.AlertType.ERROR, "Failure", "Could not remove the flight.");
            }
        }
    }

    @FXML
    void handleRefresh() {
        loadFlights();
    }
}