package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AdresseRequest {
    public final String userEmail;
    public final String vorname;
    public final String nachname;
    public final String strasse;
    public final String plz;
    public final String ort;
    public final String land;

    public AdresseRequest(
            @JsonProperty("userEmail") String userEmail,
            @JsonProperty("vorname") String vorname,
            @JsonProperty("nachname") String nachname,
            @JsonProperty("strasse") String strasse,
            @JsonProperty("plz") String plz,
            @JsonProperty("ort") String ort,
            @JsonProperty("land") String land) {
        this.userEmail = userEmail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.land = land;
    }
}
