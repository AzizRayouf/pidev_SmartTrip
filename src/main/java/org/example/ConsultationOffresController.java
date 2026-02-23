package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.entities.Offre;
import org.example.services.OffreService;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class ConsultationOffresController {

    @FXML private FlowPane containerCards;
    @FXML private TextField txtSearch;

    private OffreService os = new OffreService();

    @FXML
    public void initialize() {
        displayOffres("");

        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            displayOffres(newVal);
        });
    }

    public void displayOffres(String filter) {
        containerCards.getChildren().clear();
        try {
            List<Offre> list = os.afficher();
            for (Offre o : list) {
                if (o.getTitre().toLowerCase().contains(filter.toLowerCase())) {
                    containerCards.getChildren().add(createCard(o));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private VBox createCard(Offre o) {
        VBox card = new VBox(12);
        card.getStyleClass().add("offer-card");

        // --- 1. GESTION DE L'IMAGE ---
        ImageView imageView = new ImageView();
        try {
            String imageName = (o.getImage_url() == null || o.getImage_url().isEmpty()) ? "default.jpg" : o.getImage_url();
            InputStream is = getClass().getResourceAsStream("/img/" + imageName);
            if (is == null) is = getClass().getResourceAsStream("/img/default.jpg");
            imageView.setImage(new Image(is));
        } catch (Exception e) {
            System.err.println("Erreur image : " + e.getMessage());
        }

        imageView.setFitWidth(180);
        imageView.setFitHeight(110);
        imageView.setPreserveRatio(false);
        Rectangle clip = new Rectangle(180, 110);
        clip.setArcWidth(20); clip.setArcHeight(20);
        imageView.setClip(clip);

        // --- 2. AFFICHAGE DU LIEU (DESTINATION) ---
        Label destinationLabel = new Label("📍 " + (o.getDestination() != null ? o.getDestination() : "Destination"));
        destinationLabel.setStyle("-fx-text-fill: #1A73E8; -fx-font-weight: bold; -fx-font-size: 13px;");

        // --- 3. CONTENU DE LA CARTE ---
        Label titre = new Label(o.getTitre().toUpperCase());
        titre.getStyleClass().add("card-title");
        titre.setWrapText(true);

        Label remise = new Label("-" + o.getTaux_remise() + "%");
        remise.getStyleClass().add("card-remise");

        // BADGE ODD 8
        HBox badgeBox = new HBox();
        if (o.isIs_local_support()) {
            Label badge = new Label("🤝 Soutien Local");
            badge.getStyleClass().add("badge-odd");
            badgeBox.getChildren().add(badge);
        }

        // --- 4. BOUTON ACTION : OUVRIR LES DÉTAILS ---
        Button btnView = new Button("En savoir plus");
        btnView.getStyleClass().add("btn-view");

        // ACTION LORS DU CLIC
        btnView.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DetailOffre.fxml"));
                Parent root = loader.load();

                // On récupère le contrôleur de la fenêtre de détails
                DetailOffreController controller = loader.getController();
                // On lui passe l'objet Offre sélectionné
                controller.setOffreData(o);

                // Ouverture dans une nouvelle fenêtre (Stage)
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL); // Bloque la fenêtre parente
                stage.setTitle("SmartTrip - Détails de l'Offre");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                System.err.println("Erreur ouverture détails : " + e.getMessage());
                e.printStackTrace();
            }
        });

        // On assemble tout
        card.getChildren().addAll(imageView, badgeBox, destinationLabel, titre, remise, btnView);

        return card;
    }

    @FXML
    private void handleSwitchToAdmin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AfficherOffres.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) containerCards.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SmartTrip - Administration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}