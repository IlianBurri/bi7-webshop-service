package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdresseDaoImpl implements AdresseDao {

    private static final String ALLE_SPALTEN =
            "adressId, userEmail, vorname, nachname, strasse, plz, ort, land";

    private final DBConnection dbConnection;

    public AdresseDaoImpl(DBConnection dbConnection) {
        if (dbConnection == null) {
            throw new IllegalArgumentException("dbConnection must not be null");
        }
        this.dbConnection = dbConnection;
    }

    @Override
    public List<AdresseEntity> ladeAdressenNachBenutzerEmail(String email) throws DaoException {
        List<AdresseEntity> adressen = new ArrayList<>();

        String sql = "SELECT " + ALLE_SPALTEN +
                     " FROM adresse WHERE userEmail = ? ORDER BY createdAt DESC";

        try (ResultSet rs = dbConnection.execute(sql, email)) {
            if (rs != null) {
                while (rs.next()) {
                    adressen.add(mapAdresse(rs));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Abrufen der Adressen", e);
        }
        return adressen;
    }

    @Override
    public AdresseEntity insert(AdresseEntity adresse) throws DaoException {
        String insertSql = "INSERT INTO adresse (userEmail, vorname, nachname, strasse, plz, ort, land) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int adressId;
        try {
            adressId = dbConnection.executeUpdateReturningGeneratedKeys(insertSql,
                    adresse.getUserEmail(), adresse.getVorname(), adresse.getNachname(),
                    adresse.getStrasse(), adresse.getPlz(), adresse.getOrt(), adresse.getLand());
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Speichern der Adresse", e);
        }
        return mitAdressId(adresse, adressId);
    }

    @Override
    public boolean aktualisiere(int adressId, AdresseEntity adresse) throws DaoException {
        String sql = "UPDATE adresse SET userEmail = ?, vorname = ?, nachname = ?, strasse = ?, " +
                "plz = ?, ort = ?, land = ? WHERE adressId = ?";
        try {
            return dbConnection.executeUpdate(sql,
                    adresse.getUserEmail(), adresse.getVorname(), adresse.getNachname(),
                    adresse.getStrasse(), adresse.getPlz(), adresse.getOrt(), adresse.getLand(), adressId) > 0;
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Aktualisieren der Adresse", e);
        }
    }

    @Override
    public boolean loesche(int adressId) throws DaoException {
        String sql = "DELETE FROM adresse WHERE adressId = ?";
        try {
            return dbConnection.executeUpdate(sql, adressId) > 0;
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Löschen der Adresse", e);
        }
    }

    @Override
    public boolean existiertIdentischeAdresse(AdresseEntity adresse) throws DaoException {
        String sql = "SELECT adressId FROM adresse " +
                "WHERE userEmail = ? AND vorname = ? AND nachname = ? AND strasse = ? " +
                "AND plz = ? AND ort = ? AND land = ?";
        try (ResultSet rs = dbConnection.execute(sql,
                adresse.getUserEmail(), adresse.getVorname(), adresse.getNachname(),
                adresse.getStrasse(), adresse.getPlz(), adresse.getOrt(), adresse.getLand())) {
            return rs != null && rs.next();
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Prüfen auf identische Adresse", e);
        }
    }

    private AdresseEntity mitAdressId(AdresseEntity adresse, int adressId) {
        return new AdresseEntity(adressId, adresse.getUserEmail(), adresse.getVorname(), adresse.getNachname(),
                adresse.getStrasse(), adresse.getPlz(), adresse.getOrt(), adresse.getLand());
    }

    private AdresseEntity mapAdresse(ResultSet rs) throws SQLException {
        return new AdresseEntity(
                rs.getInt("adressId"),
                rs.getString("userEmail"),
                rs.getString("vorname"),
                rs.getString("nachname"),
                rs.getString("strasse"),
                rs.getString("plz"),
                rs.getString("ort"),
                rs.getString("land")
        );
    }
}
