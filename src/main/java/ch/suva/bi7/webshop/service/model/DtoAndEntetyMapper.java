package ch.suva.bi7.webshop.service.model;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.UserEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;

public final class DtoAndEntetyMapper {

    private DtoAndEntetyMapper() {
    }

    //User
    public static UserDto userEntity2Dto(UserEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("UserEntity darf nicht null sein");
        }

        return new UserDto(
                entity.getUsername(),
                entity.getEmail(),
                entity.isAdmin()
        );
    }

    //Artikel
    public static ArtikelDto artikelEntity2ArtikelDto(ArtikelEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ArtikelEntity darf nicht null sein");
        }
        return new ArtikelDto(entity.getArtikelId(), entity.getName(), entity.getPreis(), entity.getBild());
    }

    public static WarenkorbDto warenkorbEntity2Dto(WarenkorbItemEntity entity) {
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

    // Bestellung
    public static BestellungDto bestellungEntity2Dto(BestellungEntity entity) {
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

    // Adresse
    public static AdresseDto adresseEntity2Dto(AdresseEntity entity) {
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
}
