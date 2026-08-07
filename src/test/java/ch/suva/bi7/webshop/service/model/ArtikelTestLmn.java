package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class ArtikelTestLmn {

    @Test
    void gueltigerArtikelKannErfolgreichErstelltWerden() {
        Artikel artikel = new Artikel(1, "Laptop", new BigDecimal("999.95"), "https://example.com/laptop.jpg");

        assertEquals(1, artikel.artikelId);
        assertEquals("Laptop", artikel.name);
        assertEquals(new BigDecimal("999.95"), artikel.preis);
        assertEquals("https://example.com/laptop.jpg", artikel.bild);
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennNameNullOderLeerIst() {
        // Beispiel mit assertAll
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

    // TODO: Trim bei Konstruktor einbauen damit aus "  Laptop  ", dann "Laptop" wird und test anpassen
    @Test
    void nameMitLeerzeichenWirdAkzeptiert() {
        Artikel artikel =
                new Artikel(1, "  Laptop  ", new BigDecimal("10.00"), "bild.jpg");

        assertEquals("  Laptop  ", artikel.name);
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennPreisNullOderNegativIst() {
        // Beispiel mit assertAll
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", null, "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", new BigDecimal("-0.01"), "bild.jpg")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Artikel(1, "Laptop", BigDecimal.ZERO, "bild.jpg"))
        );
    }

    // TODO: Was wäre die Grenze für den kleinst möglichen Preis? Selbst definieren via Validierung und Test anpassen
    @Test
    void erstellenAkzeptiertKleinenPositivenPreis() {
        assertDoesNotThrow(() ->
                new Artikel(1, "Laptop", new BigDecimal("0.0001"), "bild.jpg"));
    }

    // TODO: Aktuell gibt es keine Validierung für Bild
    @Test
    void bildDarfNullSein() {
        assertDoesNotThrow(() ->
                new Artikel(1, "Laptop", new BigDecimal("10.00"), null));
    }
    @Test
    void bildDarfLeerSein() {
        assertDoesNotThrow(() ->
                new Artikel(1, "Laptop", new BigDecimal("10.00"), ""));
    }
}
