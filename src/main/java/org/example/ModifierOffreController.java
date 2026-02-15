package org.example;

import org.example.entities.Offre;
import org.example.services.OffreService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;

public class ModifierOffreController {
    @FXML private TextField txtTitre, txtRemise;
    @FXML private TextArea txtDescription;
    @FXML private DatePicker dpDebut, dpFin;
    @FXML private ComboBox<Integer> cbVoyage;

    private OffreService os = new OffreService();
    private int idOffreActuelle;

    private boolean currentLocalSupport;
    private String currentImageUrl;

    @FXML
    public void initialize() {
        // Remplir la ComboBox avec les IDs des voyages existants
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            String req = "SELECT id_voyage FROM voyage";
            ResultSet rs = conn.createStatement().executeQuery(req);

            while (rs.next()) {
                cbVoyage.getItems().add(rs.getInt("id_voyage"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur chargement voyages : " + e.getMessage());
        }
    }

    public void setData(Offre o) {
        idOffreActuelle = o.getId_offre();
        txtTitre.setText(o.getTitre());
        txtDescription.setText(o.getDescription());
        txtRemise.setText(String.valueOf(o.getTaux_remise()));
        dpDebut.setValue(o.getDate_debut().toLocalDate());
        dpFin.setValue(o.getDate_fin().toLocalDate());
        cbVoyage.setValue(o.getId_voyage());
        this.currentLocalSupport = o.isIs_local_support();
        this.currentImageUrl = o.getImage_url();
    }

    /**
     * Contrôle de saisie identique à l'ajout pour garantir la cohérence
     */
    private boolean isSaisieValide() {
        String messageErreur = "";

        if (txtTitre.getText() == null || txtTitre.getText().trim().isEmpty()) {
            messageErreur += "- Le titre est obligatoire.\n";
        } else if (txtTitre.getText().length() < 3) {
            messageErreur += "- Le titre doit contenir au moins 3 caractères.\n";
        }

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

        if (dpDebut.getValue() == null || dpFin.getValue() == null) {
            messageErreur += "- Les dates de début et de fin sont obligatoires.\n";
        } else if (dpFin.getValue().isBefore(dpDebut.getValue())) {
            messageErreur += "- La date de fin ne peut pas être avant la date de début.\n";
        }

        if (cbVoyage.getValue() == null) {
            messageErreur += "- Veuillez sélectionner un voyage lié.\n";
        }

        if (messageErreur.length() > 0) {
            showAlert("Données invalides", messageErreur);
            return false;
        }
        return true;
    }

    @FXML
    private void handleEnregistrer() {
        if (isSaisieValide()) {
            try {
                // CORRECTION : On utilise le constructeur complet (10 paramètres avec l'ID)
                Offre o = new Offre(
                        idOffreActuelle,
                        txtTitre.getText(),
                        txtDescription.getText(),
                        Integer.parseInt(txtRemise.getText()),
                        Date.valueOf(dpDebut.getValue()),
                        Date.valueOf(dpFin.getValue()),
                        "ACTIVE",
                        cbVoyage.getValue(),
                        currentLocalSupport, // On renvoie la valeur stockée
                        currentImageUrl      // On renvoie la valeur stockée
                );

                os.modifier(o);
                System.out.println("Offre modifiée avec succès !");
                handleAnnuler();

            } catch (SQLException e) {
                showAlert("Erreur BDD", "Erreur lors de la modification : " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText("Erreur de modification");
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleAnnuler() {
        txtTitre.getScene().getWindow().hide();
    }
}