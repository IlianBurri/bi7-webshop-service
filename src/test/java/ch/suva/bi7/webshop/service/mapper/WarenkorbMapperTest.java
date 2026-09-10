package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;
import ch.suva.bi7.webshop.service.model.WarenkorbDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WarenkorbMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerte() {
        WarenkorbItemEntity entity = new WarenkorbItemEntity(
                5, "user@example.com", 7, 2, "Tastatur", new BigDecimal("49.90"), "bild.png"
        );

        WarenkorbDto dto = WarenkorbMapper.toDto(entity);

        assertEquals(entity.getWarenkorbItemId(), dto.getWarenkorbItemId());
        assertEquals(entity.getArtikelId(), dto.getArtikelId());
        assertEquals(entity.getMenge(), dto.getMenge());
        assertEquals(entity.getArtikelName(), dto.getArtikelName());
        assertEquals(entity.getArtikelPreis(), dto.getArtikelPreis());
        assertEquals(entity.getArtikelBild(), dto.getArtikelBild());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> WarenkorbMapper.toDto(null));
    }
}
