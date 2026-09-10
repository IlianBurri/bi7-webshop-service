package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.model.ArtikelDto;

public final class ArtikelMapper {

    private ArtikelMapper() {
    }

    public static ArtikelDto toDto(ArtikelEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ArtikelEntity darf nicht null sein");
        }

        return new ArtikelDto(entity.getArtikelId(), entity.getName(), entity.getPreis(), entity.getBild());
    }
}
