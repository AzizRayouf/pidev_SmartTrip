package org.example.services;

import org.example.entities.Offre;
import org.example.utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreService implements IService<Offre> {

    private Connection connection;

    public OffreService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Offre o) throws SQLException {
        String req = "INSERT INTO offre (titre, description, taux_remise, date_debut, date_fin, statut, id_voyage, is_local_support, image_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, o.getTitre());
        ps.setString(2, o.getDescription());
        ps.setInt(3, o.getTaux_remise());
        ps.setDate(4, o.getDate_debut());
        ps.setDate(5, o.getDate_fin());
        ps.setString(6, o.getStatut());
        ps.setInt(7, o.getId_voyage());
        ps.setBoolean(8, o.isIs_local_support());
        ps.setString(9, o.getImage_url()); // Ajout de l'image
        ps.executeUpdate();
    }

    @Override
    public List<Offre> afficher() throws SQLException {
        List<Offre> offres = new ArrayList<>();
        String req = "SELECT o.*, v.destination FROM offre o " +
                "JOIN voyage v ON o.id_voyage = v.id_voyage";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(req);
        while (rs.next()) {
            Offre o = new Offre();
            o.setId_offre(rs.getInt("id_offre"));
            o.setTitre(rs.getString("titre"));
            o.setDescription(rs.getString("description"));
            o.setTaux_remise(rs.getInt("taux_remise"));
            o.setDate_debut(rs.getDate("date_debut"));
            o.setDate_fin(rs.getDate("date_fin"));
            o.setStatut(rs.getString("statut"));
            o.setId_voyage(rs.getInt("id_voyage"));
            o.setIs_local_support(rs.getBoolean("is_local_support"));
            o.setImage_url(rs.getString("image_url"));
            o.setDestination(rs.getString("destination"));
            offres.add(o);
        }
        return offres;
    }

    @Override
    public void modifier(Offre o) throws SQLException {
        String req = "UPDATE offre SET titre=?, description=?, taux_remise=?, date_debut=?, date_fin=?, statut=?, id_voyage=?, is_local_support=?, image_url=? WHERE id_offre=?";
        PreparedStatement ps = connection.prepareStatement(req);
        ps.setString(1, o.getTitre());
        ps.setString(2, o.getDescription());
        ps.setInt(3, o.getTaux_remise());
        ps.setDate(4, o.getDate_debut());
        ps.setDate(5, o.getDate_fin());
        ps.setString(6, o.getStatut());
        ps.setInt(7, o.getId_voyage());
        ps.setBoolean(8, o.isIs_local_support());
        ps.setString(9, o.getImage_url()); // Ajout de l'image
        ps.setInt(10, o.getId_offre());

        int rowsUpdated = ps.executeUpdate();
        if (rowsUpdated > 0) {
            System.out.println("L'offre avec l'ID " + o.getId_offre() + " a été modifiée avec succès !");
        }
    }




    @Override
    public void supprimer(int id) throws SQLException {
        // Suppression simple basée sur l'ID
        String req = "DELETE FROM offre WHERE id_offre=?";

        PreparedStatement ps = connection.prepareStatement(req);
        ps.setInt(1, id);

        int rowsDeleted = ps.executeUpdate();
        if (rowsDeleted > 0) {
            System.out.println("L'offre avec l'ID " + id + " a été supprimée avec succès !");
        } else {
            System.out.println("Aucune offre trouvée avec l'ID : " + id);
        }


    }
}
