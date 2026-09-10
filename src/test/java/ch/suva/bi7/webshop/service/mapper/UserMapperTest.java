package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.UserEntity;
import ch.suva.bi7.webshop.service.model.UserDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerteOhnePasswort() {
        UserEntity entity = new UserEntity("anna", "anna@example.com", "geheim", true);

        UserDto dto = UserMapper.toDto(entity);

        assertEquals(entity.getUsername(), dto.getUsername());
        assertEquals(entity.getEmail(), dto.getEmail());
        assertTrue(dto.isAdmin());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> UserMapper.toDto(null));
    }
}
