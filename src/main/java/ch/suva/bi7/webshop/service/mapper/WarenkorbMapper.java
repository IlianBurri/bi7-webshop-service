package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;
import ch.suva.bi7.webshop.service.model.WarenkorbDto;

public final class WarenkorbMapper {

    private WarenkorbMapper() {
    }

    public static WarenkorbDto toDto(WarenkorbItemEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("WarenkorbItemEntity darf nicht null sein");
        }

        return new WarenkorbDto(
                entity.getWarenkorbItemId(),
                entity.getArtikelId(),
                entity.getMenge(),
                entity.getArtikelName(),
                entity.getArtikelPreis(),
                entity.getArtikelBild()
        );
    }
}
