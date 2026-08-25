package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.Artikel;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ArtikelDaoImpl implements ArtikelDao {

    private final DBConnection dbConnection;

    public ArtikelDaoImpl(DBConnection dbConnection) {
        if (dbConnection == null) {
            throw new IllegalArgumentException("dbConnection must not be null");
        }
        this.dbConnection = dbConnection;
    }

    @Override
    public List<Artikel> getAllArtikel() throws Exception {
        List<Artikel> artikelListe = new ArrayList<>();

        String sql = "SELECT artikelId, name, preis, bild FROM artikel";

        try (ResultSet rs = dbConnection.execute(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    int id = rs.getInt("artikelId");
                    String name = rs.getString("name");
                    BigDecimal preis = rs.getBigDecimal("preis");
                    String bild = rs.getString("bild");

                    artikelListe.add(new Artikel(id, name, preis, bild));
                }
            }
        }
        return artikelListe;
    }

    @Override
    public int addArtikel(String name, BigDecimal preis, String bild) throws DaoException {
        String sql = "INSERT INTO artikel (name, preis, bild) VALUES (?, ?, ?)";
        try {
            return dbConnection.executeUpdateReturningGeneratedKeys(sql, name, preis, bild);
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Speichern des Artikels", e);
        }
    }
}