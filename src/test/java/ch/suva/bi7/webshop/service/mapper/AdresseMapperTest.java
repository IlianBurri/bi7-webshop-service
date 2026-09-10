package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.model.AdresseDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AdresseMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerte() {
        AdresseEntity entity = new AdresseEntity(
                3, "user@example.com", "Anna", "Muster", "Hauptstrasse 1", "8000", "Zuerich", "Schweiz"
        );

        AdresseDto dto = AdresseMapper.toDto(entity);

        assertEquals(entity.getAdressId(), dto.getAdressId());
        assertEquals(entity.getUserEmail(), dto.getUserEmail());
        assertEquals(entity.getVorname(), dto.getVorname());
        assertEquals(entity.getNachname(), dto.getNachname());
        assertEquals(entity.getStrasse(), dto.getStrasse());
        assertEquals(entity.getPlz(), dto.getPlz());
        assertEquals(entity.getOrt(), dto.getOrt());
        assertEquals(entity.getLand(), dto.getLand());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> AdresseMapper.toDto(null));
    }
}
