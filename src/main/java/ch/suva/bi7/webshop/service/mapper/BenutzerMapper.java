package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.model.BenutzerDto;

public final class BenutzerMapper {

    private BenutzerMapper() {
    }

    public static BenutzerDto toDto(BenutzerEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("BenutzerEntity darf nicht null sein");
        }

        return new BenutzerDto(entity.getUsername(), entity.getEmail(), entity.isAdmin());
    }
}
