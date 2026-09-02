package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.Bestellung;
import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BestellungDaoImpl implements BestellungDao {

    private static final Logger logger = LoggerFactory.getLogger(BestellungDaoImpl.class);

    private final DBConnection dbConnection;

    public BestellungDaoImpl(DBConnection dbConnection) {
        if (dbConnection == null) {
            throw new IllegalArgumentException("dbConnection darf nicht null sein");
        }
        this.dbConnection = dbConnection;
    }

    @Override
    public int createBestellungWithItems(String userEmail, int adressId, BigDecimal gesamtpreis, List<WarenkorbItem> items) throws DaoException {
        String insertBestellungSql = "INSERT INTO bestellung (userEmail, adressId, gesamtpreis, bestelldatum, status) " +
                "VALUES (?, ?, ?, NOW(), 'BEZAHLT')";
        String insertItemSql = "INSERT INTO bestellposition (bestellungId, artikelId, anzahl, einzelpreis) VALUES (?, ?, ?, ?)";
        String deleteWarenkorbSql = "DELETE FROM warenkorb_item WHERE userEmail = ?";

        try {
            dbConnection.beginTransaction();
            int generatedBestellungId = dbConnection.executeUpdateReturningGeneratedKeys(insertBestellungSql, userEmail, adressId, gesamtpreis);

            for (WarenkorbItem item : items) {
                dbConnection.executeUpdate(insertItemSql, generatedBestellungId, item.getArtikelId(), item.getMenge(), item.getArtikelPreis());
            }

            dbConnection.executeUpdate(deleteWarenkorbSql, userEmail);
            dbConnection.commit();
            return generatedBestellungId;
        } catch (SQLException e) {
            rollbackQuietly();
            throw new DaoException("Fehler beim Erstellen der Bestellung für User: " + userEmail, e);
        } catch (RuntimeException e) {
            rollbackQuietly();
            throw e;
        }
    }

    private void rollbackQuietly() {
        try {
            dbConnection.rollback();
        } catch (SQLException rb) {
            logger.warn("Rollback nach fehlgeschlagener Bestellung fehlgeschlagen", rb);
        }
    }

    @Override
    public Optional<Bestellung> getBestellungById(int bestellungId) throws DaoException {
        String sql = "SELECT * FROM bestellung WHERE bestellungId = ?";
        try (ResultSet rs = dbConnection.execute(sql, bestellungId)) {
            if (rs != null && rs.next()) {
                return Optional.of(mapResultSetToBestellung(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Abrufen der Bestellung mit ID: " + bestellungId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Bestellung> getBestellungenByUserEmail(String userEmail) throws DaoException {
        List<Bestellung> bestellungen = new ArrayList<>();
        String sql = "SELECT * FROM bestellung WHERE userEmail = ? ORDER BY bestelldatum DESC";

        try (ResultSet rs = dbConnection.execute(sql, userEmail)) {
            if (rs != null) {
                while (rs.next()) {
                    bestellungen.add(mapResultSetToBestellung(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Abrufen der Bestellungen für User: " + userEmail, e);
        }
        return bestellungen;
    }

    private Bestellung mapResultSetToBestellung(ResultSet rs) throws SQLException {
        Bestellung b = new Bestellung();
        b.setBestellungId(rs.getInt("bestellungId"));
        b.setUserEmail(rs.getString("userEmail"));
        b.setAdressId(rs.getInt("adressId"));
        b.setGesamtpreis(rs.getBigDecimal("gesamtpreis"));
        b.setStatus(rs.getString("status"));
        b.setBestelltAm(rs.getTimestamp("bestelldatum"));
        return b;
    }
}