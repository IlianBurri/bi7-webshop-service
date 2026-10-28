package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WarenkorbEintragMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerte() {
        WarenkorbEintragEntity entity = EntityHelper.createWarenkorbEintragEntity(
                5, "user@example.com", 7, 2);

        WarenkorbEintragDto dto = WarenkorbEintragMapper.toDto(entity);

        assertEquals(entity.getWarenkorbItemId(), dto.getWarenkorbItemId());
        assertEquals(entity.getArtikelId(), dto.getArtikelId());
        assertEquals(entity.getMenge(), dto.getMenge());
        // Artikeldaten setzt erst der Controller via ArtikelService
        assertNull(dto.getArtikelName());
        assertNull(dto.getArtikelPreis());
        assertNull(dto.getArtikelBild());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> WarenkorbEintragMapper.toDto(null));
    }
}
