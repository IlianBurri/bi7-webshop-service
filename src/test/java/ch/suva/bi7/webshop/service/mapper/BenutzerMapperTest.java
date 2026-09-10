package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.model.BenutzerDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BenutzerMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerteOhnePasswort() {
        BenutzerEntity entity = new BenutzerEntity("anna", "anna@example.com", "geheim", true);

        BenutzerDto dto = BenutzerMapper.toDto(entity);

        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertTrue(dto.isAdmin());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> BenutzerMapper.toDto(null));
    }
}
