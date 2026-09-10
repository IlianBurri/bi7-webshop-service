package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;

public final class WarenkorbEintragMapper {

    private WarenkorbEintragMapper() {
    }

    public static WarenkorbEintragDto toDto(WarenkorbEintragEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("WarenkorbEintragEntity darf nicht null sein");
        }

        return new WarenkorbEintragDto(
                entity.getWarenkorbItemId(),
                entity.getArtikelId(),
                entity.getMenge(),
                entity.getArtikelName(),
                entity.getArtikelPreis(),
                entity.getArtikelBild()
        );
    }
}
