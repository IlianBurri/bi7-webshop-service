package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.model.AdresseDto;

public final class AdresseMapper {

    private AdresseMapper() {
    }

    public static AdresseDto toDto(AdresseEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("AdresseEntity darf nicht null sein");
        }

        return new AdresseDto(
                entity.getAdressId(),
                entity.getUserEmail(),
                entity.getVorname(),
                entity.getNachname(),
                entity.getStrasse(),
                entity.getPlz(),
                entity.getOrt(),
                entity.getLand()
        );
    }

    public static AdresseEntity toEntity(AdresseDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("AdresseDto darf nicht null sein");
        }

        return new AdresseEntity(
                dto.getUserEmail(),
                dto.getVorname(),
                dto.getNachname(),
                dto.getStrasse(),
                dto.getPlz(),
                dto.getOrt(),
                dto.getLand()
        );
    }
}
