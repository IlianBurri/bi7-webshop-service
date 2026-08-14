package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.Adresse;

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
    public List<Adresse> findByUserEmail(String email) throws DaoException {
        List<Adresse> adressen = new ArrayList<>();

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
    public Adresse insert(Adresse adresse) throws DaoException {
        String insertSql = "INSERT INTO adresse (userEmail, vorname, nachname, strasse, plz, ort, land) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        int adressId;
        try {
            // Die neue adressId kommt direkt aus JDBC (AUTO_INCREMENT) – kein
            // nachgelagerter SELECT nötig, der bei Parallelität den falschen
            // Datensatz liefern könnte.
            adressId = dbConnection.executeUpdateReturningGeneratedKeys(insertSql,
                    adresse.userEmail(), adresse.vorname(), adresse.nachname(),
                    adresse.strasse(), adresse.plz(), adresse.ort(), adresse.land());
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Speichern der Adresse", e);
        }
        return mitAdressId(adresse, adressId);
    }

    @Override
    public boolean update(int adressId, Adresse adresse) throws DaoException {
        String sql = "UPDATE adresse SET userEmail = ?, vorname = ?, nachname = ?, strasse = ?, " +
                "plz = ?, ort = ?, land = ? WHERE adressId = ?";
        try {
            return dbConnection.executeUpdate(sql,
                    adresse.userEmail(), adresse.vorname(), adresse.nachname(),
                    adresse.strasse(), adresse.plz(), adresse.ort(), adresse.land(), adressId) > 0;
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Aktualisieren der Adresse", e);
        }
    }

    @Override
    public boolean delete(int adressId) throws DaoException {
        String sql = "DELETE FROM adresse WHERE adressId = ?";
        try {
            return dbConnection.executeUpdate(sql, adressId) > 0;
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Löschen der Adresse", e);
        }
    }

    @Override
    public boolean existsIdentical(Adresse adresse) throws DaoException {
        String sql = "SELECT adressId FROM adresse " +
                "WHERE userEmail = ? AND vorname = ? AND nachname = ? AND strasse = ? " +
                "AND plz = ? AND ort = ? AND land = ?";
        try (ResultSet rs = dbConnection.execute(sql,
                adresse.userEmail(), adresse.vorname(), adresse.nachname(),
                adresse.strasse(), adresse.plz(), adresse.ort(), adresse.land())) {
            return rs != null && rs.next();
        } catch (SQLException e) {
            throw new DaoException("Fehler beim Prüfen auf identische Adresse", e);
        }
    }

    private Adresse mitAdressId(Adresse adresse, int adressId) {
        return new Adresse(adressId, adresse.userEmail(), adresse.vorname(), adresse.nachname(),
                adresse.strasse(), adresse.plz(), adresse.ort(), adresse.land());
    }

    private Adresse mapAdresse(ResultSet rs) throws SQLException {
        return new Adresse(
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
