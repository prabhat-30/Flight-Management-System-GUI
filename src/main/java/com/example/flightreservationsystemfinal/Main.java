package com.example.flightreservationsystemfinal;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        primaryStage.setTitle("SkyPass Airlines Reservation System");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    public static void changeScene(String fxml) throws IOException {
        Parent pane = FXMLLoader.load(Main.class.getResource(fxml));
        mainStage.getScene().setRoot(pane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}