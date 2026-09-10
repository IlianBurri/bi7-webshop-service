package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WarenkorbEintragMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerte() {
        WarenkorbEintragEntity entity = new WarenkorbEintragEntity(
                5, "user@example.com", 7, 2, "Tastatur", new BigDecimal("49.90"), "bild.png"
        );

        WarenkorbEintragDto dto = WarenkorbEintragMapper.toDto(entity);

        assertEquals(entity.getWarenkorbItemId(), dto.getWarenkorbItemId());
        assertEquals(entity.getArtikelId(), dto.getArtikelId());
        assertEquals(entity.getMenge(), dto.getMenge());
        assertEquals(entity.getArtikelName(), dto.getArtikelName());
        assertEquals(entity.getArtikelPreis(), dto.getArtikelPreis());
        assertEquals(entity.getArtikelBild(), dto.getArtikelBild());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> WarenkorbEintragMapper.toDto(null));
    }
}
