package org.example.entities;

import java.sql.Date;

public class Offre {
    private int id_offre;
    private String titre;
    private String description;
    private int taux_remise;
    private Date date_debut;
    private Date date_fin;
    private String statut;
    private int id_voyage;
    private boolean is_local_support;
    private String image_url; // Nouvel attribut
    private String destination;
    // --- Constructeurs ---

    public Offre() {
    }

    // Constructeur complet (utile pour la récupération depuis la BDD)
    public Offre(int id_offre, String titre, String description, int taux_remise, Date date_debut, Date date_fin, String statut, int id_voyage, boolean is_local_support, String image_url) {
        this.id_offre = id_offre;
        this.titre = titre;
        this.description = description;
        this.taux_remise = taux_remise;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.statut = statut;
        this.id_voyage = id_voyage;
        this.is_local_support = is_local_support;
        this.image_url = image_url;
    }

    // Constructeur sans ID (utile pour l'insertion/ajout)
    public Offre(String titre, String description, int taux_remise, Date date_debut, Date date_fin, String statut, int id_voyage, boolean is_local_support, String image_url) {
        this.titre = titre;
        this.description = description;
        this.taux_remise = taux_remise;
        this.date_debut = date_debut;
        this.date_fin = date_fin;
        this.statut = statut;
        this.id_voyage = id_voyage;
        this.is_local_support = is_local_support;
        this.image_url = image_url;
    }

    // --- Getters et Setters ---

    public int getId_offre() { return id_offre; }
    public void setId_offre(int id_offre) { this.id_offre = id_offre; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getTaux_remise() { return taux_remise; }
    public void setTaux_remise(int taux_remise) { this.taux_remise = taux_remise; }

    public Date getDate_debut() { return date_debut; }
    public void setDate_debut(Date date_debut) { this.date_debut = date_debut; }

    public Date getDate_fin() { return date_fin; }
    public void setDate_fin(Date date_fin) { this.date_fin = date_fin; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public int getId_voyage() { return id_voyage; }
    public void setId_voyage(int id_voyage) { this.id_voyage = id_voyage; }

    public boolean isIs_local_support() { return is_local_support; }
    public void setIs_local_support(boolean is_local_support) { this.is_local_support = is_local_support; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    // --- Méthode toString (pour le debug) ---

    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id_offre +
                ", titre='" + titre + '\'' +
                ", remise=" + taux_remise + "%" +
                ", local=" + is_local_support +
                ", img='" + image_url + '\'' +
                "}\n";
    }
}