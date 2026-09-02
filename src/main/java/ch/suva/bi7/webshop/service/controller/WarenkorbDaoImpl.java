package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class WarenkorbDaoImpl implements WarenkorbDao {

    private final DBConnection dbConnection;

    public WarenkorbDaoImpl(DBConnection dbConnection) {
        if (dbConnection == null) {
            throw new IllegalArgumentException("dbConnection must not be null");
        }
        this.dbConnection = dbConnection;
    }

    @Override
    public List<WarenkorbItem> getWarenkorbByUser(String email) throws DaoException {
        List<WarenkorbItem> items = new ArrayList<>();

        String sql = "SELECT w.warenkorbItemId, w.userEmail, w.artikelId, w.menge, " +
                "a.name AS artikelName, a.preis AS artikelPreis, a.bild AS artikelBild " +
                "FROM warenkorb_item w " +
                "JOIN artikel a ON w.artikelId = a.artikelId " +
                "WHERE w.userEmail = ?";

        try (ResultSet rs = dbConnection.execute(sql, email)) {
            if (rs != null) {
                while (rs.next()) {
                    Integer warenkorbItemId = rs.getInt("warenkorbItemId");
                    String userEmail = rs.getString("userEmail");
                    Integer artikelId = rs.getInt("artikelId");
                    Integer menge = rs.getInt("menge");
                    String artikelName = rs.getString("artikelName");
                    BigDecimal artikelPreis = rs.getBigDecimal("artikelPreis");
                    String artikelBild = rs.getString("artikelBild");

                    items.add(new WarenkorbItem(
                            warenkorbItemId, userEmail, artikelId, menge,
                            artikelName, artikelPreis, artikelBild
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Abrufen des Warenkorbs", e);
        }
        return items;
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) throws DaoException {
        String sql = "INSERT INTO warenkorb_item (userEmail, artikelId, menge) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE menge = menge + VALUES(menge)";
        try {
            dbConnection.executeUpdate(sql, email, artikelId, menge);
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Hinzufügen zum Warenkorb", e);
        }
    }

    @Override
    public boolean updateMenge(int warenkorbItemId, int menge) throws DaoException {
        String sql = "UPDATE warenkorb_item SET menge = ? WHERE warenkorbItemId = ?";
        try {
            return dbConnection.executeUpdate(sql, menge, warenkorbItemId) > 0;
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Aktualisieren der Menge", e);
        }
    }

    @Override
    public boolean deleteWarenkorbItem(int warenkorbItemId) throws DaoException {
        String sql = "DELETE FROM warenkorb_item WHERE warenkorbItemId = ?";
        try {
            return dbConnection.executeUpdate(sql, warenkorbItemId) > 0;
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Löschen des Warenkorb-Items", e);
        }
    }

    @Override
    public boolean clearWarenkorbByUser(String email) throws DaoException {
        String sql = "DELETE FROM warenkorb_item WHERE userEmail = ?";
        try {
            return dbConnection.executeUpdate(sql, email) > 0;
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Leeren des Warenkorbs für den Benutzer " + email, e);
        }
    }
}