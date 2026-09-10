package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.UserEntity;
import ch.suva.bi7.webshop.service.model.UserDto;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(UserEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("UserEntity darf nicht null sein");
        }

        return new UserDto(entity.getUsername(), entity.getEmail(), entity.isAdmin());
    }
}
