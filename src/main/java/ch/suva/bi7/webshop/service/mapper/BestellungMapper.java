package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.model.BestellungDto;

public final class BestellungMapper {

    private BestellungMapper() {
    }

    public static BestellungDto toDto(BestellungEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("BestellungEntity darf nicht null sein");
        }

        return new BestellungDto(
                entity.getBestellungId(),
                entity.getUserEmail(),
                entity.getAdressId(),
                entity.getGesamtpreis(),
                entity.getStatus(),
                entity.getBestelltAm()
        );
    }
}
