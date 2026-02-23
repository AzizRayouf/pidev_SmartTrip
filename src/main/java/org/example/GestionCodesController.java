package org.example;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.entities.CodePromo;
import org.example.entities.Offre;
import org.example.services.CodePromoService;

import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class GestionCodesController {

    @FXML private Label lblTitreOffre;
    @FXML private TextField txtCode;
    @FXML private DatePicker dpExpiration;
    @FXML private TableView<CodePromo> tableCodes;
    @FXML private TableColumn<CodePromo, String> colCode;
    @FXML private TableColumn<CodePromo, Date> colExpire;

    private CodePromoService cs = new CodePromoService();
    private Offre currentOffre;

    public void setOffre(Offre o) {
        this.currentOffre = o;
        lblTitreOffre.setText("Coupons pour : " + o.getTitre());
        refreshTable();
    }

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(new PropertyValueFactory<>("code_texte"));
        colExpire.setCellValueFactory(new PropertyValueFactory<>("date_expiration"));
    }

    private void refreshTable() {
        try {
            // MÉTIER : On filtre les codes pour n'afficher que ceux de l'offre sélectionnée
            List<CodePromo> allCodes = cs.afficher();
            List<CodePromo> filtered = allCodes.stream()
                    .filter(c -> c.getId_offre() == currentOffre.getId_offre())
                    .collect(Collectors.toList());
            tableCodes.setItems(FXCollections.observableArrayList(filtered));
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // --- LOGIQUE MÉTIER ---
    @FXML
    private void handleGenererMetier() {
        // Appelle la méthode métier de génération aléatoire
        String codeGenere = cs.genererCodeAutomatique();
        txtCode.setText(codeGenere);
    }

    @FXML
    private void handleAjouter() {
        if (txtCode.getText().isEmpty() || dpExpiration.getValue() == null) {
            showAlert("Erreur", "Remplissez tous les champs.");
            return;
        }
        try {
            CodePromo cp = new CodePromo(txtCode.getText(), Date.valueOf(dpExpiration.getValue()), currentOffre.getId_offre());
            cs.ajouter(cp);
            refreshTable();
            txtCode.clear();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSupprimer() {
        CodePromo selected = tableCodes.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                cs.supprimer(selected.getId_code());
                refreshTable();
            } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    // --- LOGIQUE API (Génération PDF) ---
    @FXML
    private void handleExportPDF() {
        CodePromo selected = tableCodes.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Sélection requise", "Choisissez un coupon à exporter.");
            return;
        }

        try {
            Document document = new Document();
            String fileName = "Coupon_" + selected.getCode_texte() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));

            document.open();
            document.add(new Paragraph("--- SMARTTRIP : BON DE RÉDUCTION ---"));
            document.add(new Paragraph("Offre : " + currentOffre.getTitre()));
            document.add(new Paragraph("CODE PRIVILÈGE : " + selected.getCode_texte()));
            document.add(new Paragraph("Remise : -" + currentOffre.getTaux_remise() + "%"));
            document.add(new Paragraph("Valable jusqu'au : " + selected.getDate_expiration()));
            document.add(new Paragraph("\nMerci de soutenir l'économie locale (ODD 8)."));
            document.close();

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setContentText("PDF généré avec succès : " + fileName);
            a.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void showAlert(String t, String c) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(t); a.setContentText(c); a.show();
    }
}