package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.entities.Offre;
import org.example.services.OffreService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AfficherOffresController {

    @FXML
    private TableView<Offre> tableOffres;
    @FXML
    private TableColumn<Offre, Integer> colId;
    @FXML
    private TableColumn<Offre, String> colTitre;
    @FXML
    private TableColumn<Offre, Integer> colRemise;
    @FXML
    private TableColumn<Offre, String> colStatut;

    // --- COLONNE ODD 8 ---
    @FXML
    private TableColumn<Offre, Boolean> colODD;

    @FXML
    private TextField txtRecherche;

    private OffreService os = new OffreService();
    private ObservableList<Offre> masterData = FXCollections.observableArrayList();

    public void initialize() {
        try {
            // 1. Liaison des colonnes classiques
            colId.setCellValueFactory(new PropertyValueFactory<>("id_offre"));
            colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
            colRemise.setCellValueFactory(new PropertyValueFactory<>("taux_remise"));
            colStatut.setCellValueFactory(new PropertyValueFactory<>("statut"));

            // 2. LIAISON ET STYLISATION DE LA COLONNE ODD 8
            colODD.setCellValueFactory(new PropertyValueFactory<>("is_local_support"));

            colODD.setCellFactory(column -> {
                return new TableCell<Offre, Boolean>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setStyle("");
                        } else {
                            if (item) {
                                setText("🤝 Soutien Local");
                                setStyle("-fx-background-color: #D5F5E3; " +
                                        "-fx-text-fill: #27AE60; " +
                                        "-fx-font-weight: bold; " +
                                        "-fx-alignment: center; " +
                                        "-fx-background-radius: 15; " +
                                        "-fx-padding: 2 10; " +
                                        "-fx-font-size: 11px;");
                            } else {
                                setText("Standard");
                                setStyle("-fx-text-fill: #7F8C8D; -fx-alignment: center;");
                            }
                        }
                    }
                };
            });

            // 3. Chargement initial des données
            refreshTable();

            // 4. Configuration du Placeholder
            tableOffres.setPlaceholder(new Label("Aucune offre promotionnelle trouvée."));

        } catch (SQLException e) {
            System.err.println("Erreur d'affichage : " + e.getMessage());
        }
    }

    public void refreshTable() throws SQLException {
        masterData.setAll(os.afficher());

        FilteredList<Offre> filteredData = new FilteredList<>(masterData, b -> true);

        txtRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(offre -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (offre.getTitre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (String.valueOf(offre.getTaux_remise()).contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Offre> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableOffres.comparatorProperty());
        tableOffres.setItems(sortedData);
    }

    // --- NOUVELLE MÉTHODE : Gérer la 2ème entité (CodePromo) ---
    @FXML
    private void handleGererCodes() {
        Offre selected = tableOffres.getSelectionModel().getSelectedItem();

        if (selected != null) {
            try {
                // Charger le FXML de la gestion des codes
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GestionCodes.fxml"));
                Parent root = loader.load();

                // Envoyer l'offre sélectionnée au nouveau contrôleur
                GestionCodesController controller = loader.getController();
                controller.setOffre(selected);

                // Ouvrir la nouvelle fenêtre
                Stage stage = new Stage();
                stage.setTitle("Gestion des Codes Coupons : " + selected.getTitre());
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                showAlert("Erreur", "Impossible de charger l'interface de gestion des codes.");
                e.printStackTrace();
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner une offre pour en gérer les coupons.");
        }
    }

    @FXML
    private void handleAjouter() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AjouterOffre.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Nouvelle Offre - SmartTrip");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            refreshTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifier() {
        Offre selected = tableOffres.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModifierOffre.fxml"));
                Parent root = loader.load();
                ModifierOffreController controller = loader.getController();
                controller.setData(selected);

                Stage stage = new Stage();
                stage.setTitle("Modifier l'Offre - SmartTrip");
                stage.setScene(new Scene(root));
                stage.showAndWait();
                refreshTable();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner une offre à modifier.");
        }
    }

    @FXML
    private void handleSupprimer() {
        Offre selected = tableOffres.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer l'offre : " + selected.getTitre());
            alert.setContentText("Voulez-vous vraiment supprimer cette offre ?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    os.supprimer(selected.getId_offre());
                    refreshTable();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Sélection requise", "Veuillez sélectionner une offre à supprimer.");
        }
    }

    @FXML
    private void handleSwitchToClient() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ConsultationOffres.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableOffres.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartTrip - Espace Voyageur");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}