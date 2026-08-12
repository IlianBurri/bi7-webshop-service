package ch.suva.bi7.webshop.service.model;

public record Adresse(
        int adressId,
        String userEmail,
        String vorname,
        String nachname,
        String strasse,
        String plz,
        String ort,
        String land) {
}
