package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelTest {

    @Test
    void gueltigerArtikelKannErfolgreichErstelltWerden() {
        Artikel artikel = new Artikel(1, "  Laptop  ", new BigDecimal("999.95"), "  https://example.com/laptop.jpg  ");

        assertEquals(1, artikel.artikelId);
        assertEquals("Laptop", artikel.name);
        assertEquals(new BigDecimal("999.95"), artikel.preis);
        assertEquals("https://example.com/laptop.jpg", artikel.bild);
    }

    @Test
    void ungueltigeEingabenWerdenAbgelehnt() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(null, "Laptop", new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, null, new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "   ", new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", null, "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", BigDecimal.ZERO, "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("-0.01"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("0.009"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("10.00"), "")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("10.00"), "   "))
        );
    }

    @Test
    void erlaubteGrenzfaelleWerdenAkzeptiert() {
        assertAll(
                () -> assertDoesNotThrow(() ->
                        new Artikel(1, "Laptop", new BigDecimal("0.01"), "bild.jpg")),
                () -> assertDoesNotThrow(() ->
                        new Artikel(1, "Laptop", new BigDecimal("10.00"), null))
        );
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennNameNullOderLeerIst() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, null, new BigDecimal("10.00"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "   ", new BigDecimal("10.00"), "bild.jpg"))
        );
    }

    @Test
    void leererNameLiefertErwarteteFehlermeldung() {
        IllegalArgumentException ex =
                assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "", new BigDecimal("10.00"), "bild.jpg"));

        assertEquals("Name darf nicht null/leer sein", ex.getMessage());
    }

    @Test
    void nameMitLeerzeichenWirdGetrimmt() {
        Artikel artikel =
                new Artikel(1, "  Laptop  ", new BigDecimal("10.00"), "bild.jpg");

        assertEquals("Laptop", artikel.name);
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennPreisNullOderNegativIst() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", null, "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("-0.01"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", BigDecimal.ZERO, "bild.jpg"))
        );
    }

    @Test
    void preisUnterMindestpreisWirdAbgelehnt() {
        assertThrows(IllegalArgumentException.class,
                () -> new Artikel(1, "Laptop", new BigDecimal("0.0001"), "bild.jpg"));
    }

    @Test
    void bildDarfNullSein() {
        assertDoesNotThrow(() ->
                new Artikel(1, "Laptop", new BigDecimal("10.00"), null));
    }

    @Test
    void bildDarfNichtLeerSein() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("10.00"), "")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("10.00"), "   "))
        );
    }
}
