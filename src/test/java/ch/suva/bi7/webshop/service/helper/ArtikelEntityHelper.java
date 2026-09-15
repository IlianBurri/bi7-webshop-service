package ch.suva.bi7.webshop.service.helper;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;

import java.math.BigDecimal;

public class ArtikelEntityHelper {

    private ArtikelEntityHelper() {
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
}
