package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdresseDto {

    private final Integer adressId;
    private final String userEmail;
    private final String vorname;
    private final String nachname;
    private final String strasse;
    private final String plz;
    private final String ort;
    private final String land;

    public AdresseDto(
            @JsonProperty("adressId") Integer adressId,
            @JsonProperty("userEmail") String userEmail,
            @JsonProperty("vorname") String vorname,
            @JsonProperty("nachname") String nachname,
            @JsonProperty("strasse") String strasse,
            @JsonProperty("plz") String plz,
            @JsonProperty("ort") String ort,
            @JsonProperty("land") String land) {
        this.adressId = adressId;
        this.userEmail = userEmail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.land = land;
    }

    public Integer getAdressId() {
        return adressId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getStrasse() {
        return strasse;
    }

    public String getPlz() {
        return plz;
    }

    public String getOrt() {
        return ort;
    }

    public String getLand() {
        return land;
    }
}
