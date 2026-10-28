package ch.suva.bi7.webshop.service.helper;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EntityHelperTest {

    @Test
    void erstellenWirftIllegalArgumentExceptionWennArtikelIdUngueltigIst() {
        assertAll(
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(
                                null, "iPhone 15", new BigDecimal("1.00"), null)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(
                                0, "iPhone 16", new BigDecimal("1.00"), null)
                ),
                () -> assertThrows(
                        IllegalArgumentException.class,
                        () -> EntityHelper.createArtikelEntity(
                                -1, "iPhone", new BigDecimal("1.00"), null)
                )
        );
    }

    @Test
    void ungueltigeArtikelIdLiefertErwarteteFehlermeldung() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> EntityHelper.createArtikelEntity(
                        null, "iPhone", new BigDecimal("1.00"), null
                )
        );

        assertEquals(
                "artikelId darf nicht null oder kleiner 1 sein",
                ex.getMessage()
        );
    }
}
