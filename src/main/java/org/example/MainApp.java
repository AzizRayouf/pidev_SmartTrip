package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Chemin exact vers ton fichier FXML dans resources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AfficherOffres.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("SmartTrip - Gestion des Offres");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}