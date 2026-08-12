package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.DBConnection;
import ch.suva.bi7.webshop.service.model.Adresse;

import java.sql.ResultSet;
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
    public List<Adresse> findByUserEmail(String email) throws Exception {
        List<Adresse> adressen = new ArrayList<>();

        String sql = "SELECT " + ALLE_SPALTEN +
                     " FROM adresse WHERE userEmail = '" + email + "' ORDER BY createdAt DESC";

        try (ResultSet rs = dbConnection.execute(sql)) {
            if (rs != null) {
                while (rs.next()) {
                    adressen.add(mapAdresse(rs));
                }
            }
        }
        return adressen;
    }

    @Override
    public Adresse insert(Adresse adresse) throws Exception {
        String insertSql = "INSERT INTO adresse (userEmail, vorname, nachname, strasse, plz, ort, land) " +
                "VALUES ('" + adresse.userEmail() + "', '" + adresse.vorname() + "', '" + adresse.nachname() + "', '" +
                adresse.strasse() + "', '" + adresse.plz() + "', '" + adresse.ort() + "', '" + adresse.land() + "')";
        dbConnection.executeUpdate(insertSql);

        String selectSql = "SELECT " + ALLE_SPALTEN + " FROM adresse " +
                "WHERE userEmail = '" + adresse.userEmail() + "' AND vorname = '" + adresse.vorname() +
                "' AND nachname = '" + adresse.nachname() + "' AND strasse = '" + adresse.strasse() +
                "' AND plz = '" + adresse.plz() + "' AND ort = '" + adresse.ort() + "' AND land = '" + adresse.land() +
                "' ORDER BY createdAt DESC LIMIT 1";
        try (ResultSet rs = dbConnection.execute(selectSql)) {
            if (rs != null && rs.next()) {
                return mapAdresse(rs);
            }
        }
        return adresse;
    }

    @Override
    public boolean update(int adressId, Adresse adresse) throws Exception {
        String sql = "UPDATE adresse SET userEmail = '" + adresse.userEmail() + "', vorname = '" + adresse.vorname() +
                "', nachname = '" + adresse.nachname() + "', strasse = '" + adresse.strasse() +
                "', plz = '" + adresse.plz() + "', ort = '" + adresse.ort() + "', land = '" + adresse.land() +
                "' WHERE adressId = " + adressId;
        return dbConnection.executeUpdate(sql) > 0;
    }

    @Override
    public boolean delete(int adressId) throws Exception {
        String sql = "DELETE FROM adresse WHERE adressId = " + adressId;
        return dbConnection.executeUpdate(sql) > 0;
    }

    @Override
    public boolean existsIdentical(Adresse adresse) throws Exception {
        String sql = "SELECT adressId FROM adresse " +
                "WHERE userEmail = '" + adresse.userEmail() + "' AND vorname = '" + adresse.vorname() +
                "' AND nachname = '" + adresse.nachname() + "' AND strasse = '" + adresse.strasse() +
                "' AND plz = '" + adresse.plz() + "' AND ort = '" + adresse.ort() + "' AND land = '" + adresse.land() + "'";
        try (ResultSet rs = dbConnection.execute(sql)) {
            return rs != null && rs.next();
        }
    }

    private Adresse mapAdresse(ResultSet rs) throws Exception {
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
