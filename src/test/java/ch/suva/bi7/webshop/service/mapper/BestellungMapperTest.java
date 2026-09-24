package ch.suva.bi7.webshop.service.mapper;

import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungStatusEnum;
import ch.suva.bi7.webshop.service.model.BestellungDto;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Timestamp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BestellungMapperTest {

    @Test
    void mapToDtoUebernimmtAlleWerte() {
        Timestamp bestelltAm = Timestamp.valueOf("2026-09-10 09:54:36");
        BestellungEntity entity = EntityHelper.createBestellungEntity(
                11, "user@example.com", 3, new BigDecimal("99.90"), BestellungStatusEnum.BEZAHLT, bestelltAm
        );

        BestellungDto dto = BestellungMapper.toDto(entity);

        assertEquals(entity.getBestellungId(), dto.getBestellungId());
        assertEquals(entity.getUserEmail(), dto.getUserEmail());
        assertEquals(entity.getAdressId(), dto.getAdressId());
        assertEquals(entity.getGesamtpreis(), dto.getGesamtpreis());
        assertEquals(entity.getStatus().name(), dto.getStatus());
        assertEquals(entity.getBestelltAm(), dto.getBestelltAm());
    }

    @Test
    void mapToDtoLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> BestellungMapper.toDto(null));
    }
}
