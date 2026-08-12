package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;

import java.math.BigDecimal;
import java.sql.ResultSet;
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
    public List<WarenkorbItem> getWarenkorbByUser(String email) throws Exception {
        List<WarenkorbItem> items = new ArrayList<>();

        String sql = "SELECT w.warenkorbItemId, w.userEmail, w.artikelId, w.menge, " +
                     "a.name AS artikelName, a.preis AS artikelPreis, a.bild AS artikelBild " +
                     "FROM warenkorb_item w " +
                     "JOIN artikel a ON w.artikelId = a.artikelId " +
                     "WHERE w.userEmail = '" + email + "'";

        try (ResultSet rs = dbConnection.execute(sql)) {
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
        }
        return items;
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) throws Exception {
        // Prüfe zuerst ob Item bereits im Warenkorb
        String checkSql = "SELECT warenkorbItemId, menge FROM warenkorb_item " +
                          "WHERE userEmail = '" + email + "' AND artikelId = " + artikelId;

        try (ResultSet rs = dbConnection.execute(checkSql)) {
            if (rs != null && rs.next()) {
                // Item existiert bereits -> erhöhe Menge um die gewählte Menge
                int warenkorbItemId = rs.getInt("warenkorbItemId");
                int currentMenge = rs.getInt("menge");
                int newMenge = currentMenge + menge;

                String updateSql = "UPDATE warenkorb_item SET menge = " + newMenge +
                                   " WHERE warenkorbItemId = " + warenkorbItemId;
                dbConnection.executeUpdate(updateSql);
            } else {
                // Item existiert nicht -> füge mit gewählter Menge hinzu
                String insertSql = "INSERT INTO warenkorb_item (userEmail, artikelId, menge) " +
                                   "VALUES ('" + email + "', " + artikelId + ", " + menge + ")";
                dbConnection.executeUpdate(insertSql);
            }
        }
    }

    @Override
    public void updateMenge(int warenkorbItemId, int menge) throws Exception {
        String sql = "UPDATE warenkorb_item SET menge = " + menge +
                     " WHERE warenkorbItemId = " + warenkorbItemId;
        dbConnection.executeUpdate(sql);
    }

    @Override
    public void deleteWarenkorbItem(int warenkorbItemId) throws Exception {
        String sql = "DELETE FROM warenkorb_item WHERE warenkorbItemId = " + warenkorbItemId;
        dbConnection.executeUpdate(sql);
    }
}
