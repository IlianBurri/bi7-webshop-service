package ch.suva.bi7.webshop.service.db.entity;

public record AdresseEntity(
        int adressId,
        String userEmail,
        String vorname,
        String nachname,
        String strasse,
        String plz,
        String ort,
        String land) {
}
