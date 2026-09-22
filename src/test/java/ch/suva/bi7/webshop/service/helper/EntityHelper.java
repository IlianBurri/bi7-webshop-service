package ch.suva.bi7.webshop.service.helper;

import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellPositionEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungStatus;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class EntityHelper {

    private EntityHelper() {
        // Private constructor to prevent instantiation
    }

    public static ArtikelEntity createArtikelEntity(Integer artikelId, String name, BigDecimal preis, String bild) {
        if (artikelId == null || artikelId.intValue() <= 0) {
            throw new IllegalArgumentException("artikelId darf nicht null oder klein 1 sein");
        }
        ArtikelEntity artikelEntity = new ArtikelEntity(name, preis, bild);
        setArtikelId(artikelEntity, artikelId);
        return artikelEntity;
    }

    public static void setArtikelId(ArtikelEntity artikelEntity, Integer artikelId) {
        // Griff in die Trickkiste, um die private artikelId zu setzen, da der Konstruktor sie nicht akzeptiert.
        // JPA/Hibernate setzen die ID normalerweise automatisch, aber für Testzwecke setzen wir sie manuell.
        try {
            java.lang.reflect.Field field = ArtikelEntity.class.getDeclaredField("artikelId");
            field.setAccessible(true);
            field.set(artikelEntity, artikelId); // Setze die generierte ID auf einen bestimmten Wert
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static AdresseEntity createAdresseEntity(Integer adressId, String userEmail, String vorname, String nachname, String strasse, String plz, String ort, String land) {
        if (adressId == null || adressId.intValue() <= 0) {
            throw new IllegalArgumentException("adresseId darf nicht null oder klein 1 sein");
        }
        AdresseEntity adresseEntity = new AdresseEntity(userEmail, vorname, nachname, strasse, plz, ort, land);
        setAdresseId(adresseEntity, adressId);
        return adresseEntity;
    }

    public static void setAdresseId(AdresseEntity adresseEntity, Integer adressId) {
        try {
            java.lang.reflect.Field field = AdresseEntity.class.getDeclaredField("adressId");
            field.setAccessible(true);
            field.set(adresseEntity, adressId); // Setze die generierte ID auf einen bestimmten Wert
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static BenutzerEntity createBenutzerEntity(Integer benutzerId, String username, String email, String password, Boolean isAdmin) {
        if (benutzerId == null || benutzerId.intValue() <= 0) {
            throw new IllegalArgumentException("BenutzerId darf nicht null oder klein 1 sein");
        }
        BenutzerEntity benutzerEntity = new BenutzerEntity(username, email, password, isAdmin);
        setBenutzerId(benutzerEntity, benutzerId);
        return benutzerEntity;
    }

    public static void setBenutzerId(BenutzerEntity benutzerEntity, Integer benutzerId) {
        try {
            java.lang.reflect.Field field = BenutzerEntity.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(benutzerEntity, benutzerId); // Setze die generierte ID auf einen bestimmten Wert
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static BestellungEntity createBestellungEntity(Integer bestellungId, String userEmail, Integer adressId, BigDecimal gesamtpreis, BestellungStatus status, Timestamp bestelltAm) {
        if (bestellungId == null || bestellungId.intValue() <= 0) {
            throw new IllegalArgumentException("BestellungId darf nicht null oder klein 1 sein");
        }
        BestellungEntity bestellungEntity = new BestellungEntity(userEmail, adressId, gesamtpreis, status, bestelltAm);
        setBestellungId(bestellungEntity, bestellungId);
        return bestellungEntity;
    }

    public static void setBestellungId(BestellungEntity bestellungEntity, Integer bestellungId) {
        try {
            java.lang.reflect.Field field = BestellungEntity.class.getDeclaredField("bestellungId");
            field.setAccessible(true);
            field.set(bestellungEntity, bestellungId); // Setze die generierte ID auf einen bestimmten Wert
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static BestellPositionEntity createBestellPositionEntity(Integer bestellpositionId, Integer bestellungId, Integer artikelId, Integer anzahl, BigDecimal einzelpreis) {
        if (bestellpositionId == null || bestellpositionId.intValue() <= 0) {
            throw new IllegalArgumentException("BestellpositionId darf nicht null oder klein 1 sein");
        }
        BestellPositionEntity bestellPositionEntity = new BestellPositionEntity(bestellungId, artikelId, anzahl, einzelpreis);
        setBestellpositionId(bestellPositionEntity, bestellpositionId);
        return bestellPositionEntity;
    }

    public static void setBestellpositionId(BestellPositionEntity bestellPositionEntity, Integer bestellpositionId) {
        try {
            java.lang.reflect.Field field = BestellPositionEntity.class.getDeclaredField("bestellpositionId");
            field.setAccessible(true);
            field.set(bestellPositionEntity, bestellpositionId); // Setze die generierte ID auf einen bestimmten Wert
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static WarenkorbEintragEntity createWarenkorbEintragEntity(Integer warenkorbItemId, String userEmail, Integer artikelId, Integer menge, String artikelName, BigDecimal artikelPreis, String artikelBild) {
        if (warenkorbItemId == null || warenkorbItemId.intValue() <= 0) {
            throw new IllegalArgumentException("WarenkorbItemId darf nicht null oder klein 1 sein");
        }
        WarenkorbEintragEntity warenkorbEintragEntity = new WarenkorbEintragEntity(
                userEmail, artikelId, menge, artikelName, artikelPreis, artikelBild);
        setWarenkorbItemId(warenkorbEintragEntity, warenkorbItemId);
        return warenkorbEintragEntity;
    }

    public static void setWarenkorbItemId(WarenkorbEintragEntity warenkorbEintragEntity, Integer warenkorbItemId) {
        try {
            java.lang.reflect.Field field = WarenkorbEintragEntity.class.getDeclaredField("warenkorbItemId");
            field.setAccessible(true);
            field.set(warenkorbEintragEntity, warenkorbItemId); // Setze die generierte ID auf einen bestimmten Wert
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
