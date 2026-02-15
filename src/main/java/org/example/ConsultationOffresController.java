package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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
        displayOffres(""); // Affiche toutes les offres au démarrage

        // Écouteur pour la recherche dynamique
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> {
            displayOffres(newVal);
        });
    }

    public void displayOffres(String filter) {
        containerCards.getChildren().clear();
        try {
            List<Offre> list = os.afficher();
            for (Offre o : list) {
                // Filtrer par titre
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

        // --- 2. NOUVEAU : AFFICHAGE DU LIEU (DESTINATION) ---
        // On l'affiche avec une couleur bleue et une petite icône
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

        Button btnView = new Button("En savoir plus");
        btnView.getStyleClass().add("btn-view");

        // ON ASSEMBLE TOUT (Ordre : Image -> Badge -> Lieu -> Titre -> Remise -> Bouton)
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