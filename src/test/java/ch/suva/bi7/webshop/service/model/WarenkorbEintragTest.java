package ch.suva.bi7.webshop.service.model;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbEintragTest {

    @Test
    void gueltigesWarenkorbItemKannErstelltWerden() {
        WarenkorbEintragEntity item = EntityHelper.createWarenkorbEintragEntity(
                1, "user@example.com", 5, 3,
                "iPhone 15 Pro", new BigDecimal("1199.00"), "https://example.com/bild.jpg"
        );

        assertEquals(1, item.getWarenkorbItemId());
        assertEquals("user@example.com", item.getUserEmail());
        assertEquals(5, item.getArtikelId());
        assertEquals(3, item.getMenge());
        assertEquals("iPhone 15 Pro", item.getArtikelName());
        assertEquals(new BigDecimal("1199.00"), item.getArtikelPreis());
        assertEquals("https://example.com/bild.jpg", item.getArtikelBild());
    }

    @Test
    void warenkorbItemOhneIdDarfErstelltWerden() {
        WarenkorbEintragEntity item = new WarenkorbEintragEntity(
                "user@example.com", 5, 1,
                "iPhone 15 Pro", new BigDecimal("1199.00"), null
        );

        assertNull(item.getWarenkorbItemId());
        assertNull(item.getArtikelBild());
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennEmailFehlt() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity(null, 5, 1, "iPhone", new BigDecimal("1.00"), null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity("   ", 5, 1, "iPhone", new BigDecimal("1.00"), null))
        );
    }

    @Test
    void leererEmailLiefertErwarteteFehlermeldung() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new WarenkorbEintragEntity("", 5, 1, "iPhone", new BigDecimal("1.00"), null));

        assertEquals("userEmail darf nicht null/leer sein", ex.getMessage());
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennArtikelIdUngueltigIst() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity("user@example.com", null, 1, "iPhone", new BigDecimal("1.00"), null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity("user@example.com", 0, 1, "iPhone", new BigDecimal("1.00"), null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity("user@example.com", -1, 1, "iPhone", new BigDecimal("1.00"), null))
        );
    }

    @Test
    void ungueltigeArtikelIdLiefertErwarteteFehlermeldung() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new WarenkorbEintragEntity("user@example.com", 0, 1, "iPhone", new BigDecimal("1.00"), null));

        assertEquals("artikelId muss > 0 sein", ex.getMessage());
    }

    @Test
    void erstellenWirftIllegalArgumentExceptionWennMengeUngueltigIst() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity("user@example.com", 5, null, "iPhone", new BigDecimal("1.00"), null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity("user@example.com", 5, 0, "iPhone", new BigDecimal("1.00"), null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new WarenkorbEintragEntity("user@example.com", 5, -2, "iPhone", new BigDecimal("1.00"), null))
        );
    }

    @Test
    void ungueltigeMengeLiefertErwarteteFehlermeldung() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> new WarenkorbEintragEntity("user@example.com", 5, 0, "iPhone", new BigDecimal("1.00"), null));

        assertEquals("menge muss > 0 sein", ex.getMessage());
    }
}
