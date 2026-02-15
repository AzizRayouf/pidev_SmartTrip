package org.example;

import org.example.entities.Offre;
import org.example.services.OffreService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;

public class AjouterOffreController {

    @FXML private TextField txtTitre;
    @FXML private TextArea txtDescription;
    @FXML private TextField txtRemise;
    @FXML private DatePicker dpDebut;
    @FXML private DatePicker dpFin;
    @FXML private ComboBox<Integer> cbVoyage;

    private OffreService os = new OffreService();

    @FXML
    public void initialize() {
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            String req = "SELECT id_voyage FROM voyage";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(req);

            while (rs.next()) {
                cbVoyage.getItems().add(rs.getInt("id_voyage"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement voyages : " + e.getMessage());
        }
    }

    /**
     * Méthode de contrôle de saisie détaillée
     */
    private boolean isSaisieValide() {
        String messageErreur = "";

        // 1. Vérification du Titre
        if (txtTitre.getText() == null || txtTitre.getText().trim().isEmpty()) {
            messageErreur += "- Le titre est obligatoire.\n";
        } else if (txtTitre.getText().length() < 3) {
            messageErreur += "- Le titre doit contenir au moins 3 caractères.\n";
        }

        // 2. Vérification de la Remise
        if (txtRemise.getText() == null || txtRemise.getText().trim().isEmpty()) {
            messageErreur += "- Le champ remise est obligatoire.\n";
        } else {
            try {
                int remise = Integer.parseInt(txtRemise.getText());
                if (remise < 0 || remise > 100) {
                    messageErreur += "- La remise doit être entre 0 et 100%.\n";
                }
            } catch (NumberFormatException e) {
                messageErreur += "- La remise doit être un nombre entier valide.\n";
            }
        }

        // 3. Vérification des Dates
        if (dpDebut.getValue() == null) {
            messageErreur += "- La date de début est obligatoire.\n";
        }
        if (dpFin.getValue() == null) {
            messageErreur += "- La date de fin est obligatoire.\n";
        }

        if (dpDebut.getValue() != null && dpFin.getValue() != null) {
            if (dpFin.getValue().isBefore(dpDebut.getValue())) {
                messageErreur += "- La date de fin ne peut pas être antérieure à la date de début.\n";
            }
            if (dpDebut.getValue().isBefore(LocalDate.now())) {
                messageErreur += "- La date de début ne peut pas être dans le passé.\n";
            }
        }

        // 4. Vérification de la sélection du voyage
        if (cbVoyage.getValue() == null) {
            messageErreur += "- Veuillez sélectionner un voyage lié.\n";
        }

        // Affichage des erreurs si il y en a
        if (messageErreur.length() > 0) {
            showAlert("Erreur de saisie", messageErreur);
            return false;
        }

        return true;
    }

    @FXML
    private void handleEnregistrer() {
        if (isSaisieValide()) {
            try {
                String titre = txtTitre.getText();
                String desc = txtDescription.getText();
                int remise = Integer.parseInt(txtRemise.getText());
                Date dateD = Date.valueOf(dpDebut.getValue());
                Date dateF = Date.valueOf(dpFin.getValue());
                int idV = cbVoyage.getValue();

                // CORRECTION ICI : Ajout de 'false' (pour l'ODD 8) et 'default.jpg' (pour l'image)
                // Cela doit correspondre exactement à l'ordre de ton constructeur à 9 paramètres
                Offre nouvelleOffre = new Offre(titre, desc, remise, dateD, dateF, "ACTIVE", idV, false, "default.jpg");

                os.ajouter(nouvelleOffre);

                System.out.println("Offre enregistrée avec succès !");
                handleAnnuler();

            } catch (SQLException e) {
                showAlert("Erreur BDD", "Erreur lors de l'enregistrement : " + e.getMessage());
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING); // Utilisation de WARNING pour la saisie
        alert.setTitle(title);
        alert.setHeaderText("Informations invalides");
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleAnnuler() {
        txtTitre.getScene().getWindow().hide();
    }
}