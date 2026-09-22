package ch.suva.bi7.webshop.service.model;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelTest {

    @Test
    void gueltigerArtikelKannErfolgreichErstelltWerden() {
        ArtikelEntity artikel = EntityHelper.createArtikelEntity(1, "  Laptop  ", new BigDecimal("999.95"), "  https://example.com/laptop.jpg  ");

        assertEquals(1, artikel.getArtikelId());
        assertEquals("Laptop", artikel.getName());
        assertEquals(new BigDecimal("999.95"), artikel.getPreis());
        assertEquals("https://example.com/laptop.jpg", artikel.getBild());
    }

    @Test
    void ungueltigeEingabenWerdenAbgelehnt() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(null, "Laptop", new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, null, new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "   ", new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", null, "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", BigDecimal.ZERO, "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("-0.01"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("0.009"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("10.00"), "")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("10.00"), "   "))
        );
    }

    @Test
    void erlaubteGrenzfaelleWerdenAkzeptiert() {
        assertAll(
                () -> assertDoesNotThrow(() ->
                        EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("0.01"), "bild.jpg")),
                () -> assertDoesNotThrow(() ->
                        EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("10.00"), null))
        );
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennNameNullOderLeerIst() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, null, new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "   ", new BigDecimal("10.00"), "bild.jpg"))
        );
    }

    @Test
    void leererNameLiefertErwarteteFehlermeldung() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "", new BigDecimal("10.00"), "bild.jpg"));

        assertEquals("Name darf nicht null/leer sein", ex.getMessage());
    }

    @Test
    void nameMitLeerzeichenWirdGetrimmt() {
        ArtikelEntity artikel =
                EntityHelper.createArtikelEntity(1, "  Laptop  ", new BigDecimal("10.00"), "bild.jpg");

        assertEquals("Laptop", artikel.getName());
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennPreisNullOderNegativIst() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", null, "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("-0.01"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", BigDecimal.ZERO, "bild.jpg"))
        );
    }

    @Test
    void preisUnterMindestpreisWirdAbgelehnt() {
        assertThrows(IllegalArgumentException.class,
                () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("0.0001"), "bild.jpg"));
    }

    @Test
    void bildDarfNullSein() {
        assertDoesNotThrow(() ->
                EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("10.00"), null));
    }

    @Test
    void bildDarfNichtLeerSein() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("10.00"), "")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(1, "Laptop", new BigDecimal("10.00"), "   "))
        );
    }
}
