package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.model.ArtikelDto;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ArtikelMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerte() {
        ArtikelEntity entity = new ArtikelEntity(7, "Tastatur", new BigDecimal("49.90"), "bild.png");

        ArtikelDto dto = ArtikelMapper.toDto(entity);

        assertEquals(entity.getArtikelId(), dto.getArtikelId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getPreis(), dto.getPreis());
        assertEquals(entity.getBild(), dto.getBild());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> ArtikelMapper.toDto(null));
    }
}
