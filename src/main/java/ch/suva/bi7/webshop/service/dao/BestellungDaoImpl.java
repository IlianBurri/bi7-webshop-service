package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
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
    public int erstelleBestellungMitWarenkorbItems(String userEmail, int adressId, BigDecimal gesamtpreis,
                                                   List<WarenkorbEintragEntity> warenkorbEintragEntityList) throws DaoException {
        String insertBestellungSql = "INSERT INTO bestellung (userEmail, adressId, gesamtpreis, bestelldatum, status) " +
                "VALUES (?, ?, ?, NOW(), 'BEZAHLT')";
        String insertBestellpositionSql =
                "INSERT INTO bestellposition (bestellungId, artikelId, anzahl, einzelpreis) VALUES (?, ?, ?, ?)";
        String deleteWarenkorbSql = "DELETE FROM warenkorb_item WHERE userEmail = ?";

        try {
            dbConnection.beginTransaction();
            int generatedBestellungId = dbConnection.executeUpdateReturningGeneratedKeys(insertBestellungSql, userEmail, adressId, gesamtpreis);

            for (WarenkorbEintragEntity warenkorbEintragEntity : warenkorbEintragEntityList) {
                dbConnection.executeUpdate(
                        insertBestellpositionSql,
                        generatedBestellungId,
                        warenkorbEintragEntity.getArtikelId(),
                        warenkorbEintragEntity.getMenge(),
                        warenkorbEintragEntity.getArtikelPreis()
                );
            }

            dbConnection.executeUpdate(deleteWarenkorbSql, userEmail);
            dbConnection.commit();
            return generatedBestellungId;
        } catch (SQLException e) {
            rollbackQuietly();
            throw new DaoException("Fehler beim Erstellen der Bestellung für Benutzer: " + userEmail, e);
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
    public Optional<BestellungEntity> holeBestellungNachId(int bestellungId) throws DaoException {
        String sql = "SELECT * FROM bestellung WHERE bestellungId = ?";
        try (ResultSet rs = dbConnection.execute(sql, bestellungId)) {
            if (rs != null && rs.next()) {
                return Optional.of(mappeResultSetZuBestellung(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Abrufen der Bestellung mit ID: " + bestellungId, e);
        }
        return Optional.empty();
    }

    @Override
    public List<BestellungEntity> getBestellungenNachBenutzerEmail(String userEmail) throws DaoException {
        List<BestellungEntity> bestellungen = new ArrayList<>();
        String sql = "SELECT * FROM bestellung WHERE userEmail = ? ORDER BY bestelldatum DESC";

        try (ResultSet rs = dbConnection.execute(sql, userEmail)) {
            if (rs != null) {
                while (rs.next()) {
                    bestellungen.add(mappeResultSetZuBestellung(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Abrufen der Bestellungen für Benutzer: " + userEmail, e);
        }
        return bestellungen;
    }

    private BestellungEntity mappeResultSetZuBestellung(ResultSet rs) throws SQLException {
        return new BestellungEntity(
                rs.getInt("bestellungId"),
                rs.getString("userEmail"),
                rs.getInt("adressId"),
                rs.getBigDecimal("gesamtpreis"),
                rs.getString("status"),
                rs.getTimestamp("bestelldatum")
        );
    }
}
