package ch.suva.bi7.webshop.service.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelTest {

    @Test
    void tf01_gueltigerArtikelKannErfolgreichErstelltWerden() {
        Artikel artikel = new Artikel(1, "Laptop", new BigDecimal("999.95"), "https://example.com/laptop.jpg");

        assertEquals(1, artikel.artikelId);
        assertEquals("Laptop", artikel.name);
        assertEquals(new BigDecimal("999.95"), artikel.preis);
        assertEquals("https://example.com/laptop.jpg", artikel.bild);
    }

    @Test
    void tf02_erstellenWirftIllegalArgumentExceptionWennNameNullOderLeerIst() {
        assertThrows(IllegalArgumentException.class, () ->
                new Artikel(1, null, new BigDecimal("10.00"), "bild.jpg")
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Artikel(1, "   ", new BigDecimal("10.00"), "bild.jpg")
        );
    }

    @Test
    void tf03_erstellenWirftIllegalArgumentExceptionWennPreisNullOderNegativIst() {
        assertThrows(IllegalArgumentException.class, () ->
                new Artikel(1, "Laptop", null, "bild.jpg")
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Artikel(1, "Laptop", new BigDecimal("-0.01"), "bild.jpg")
        );
    }
}