package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BestellungDto {

    private final Integer bestellungId;
    private final String userEmail;
    private final Integer adressId;
    private final BigDecimal gesamtpreis;
    private final String status;
    private final Timestamp bestelltAm;

    public BestellungDto(
            @JsonProperty("bestellungId") Integer bestellungId,
            @JsonProperty("userEmail") String userEmail,
            @JsonProperty("adressId") Integer adressId,
            @JsonProperty("gesamtpreis") BigDecimal gesamtpreis,
            @JsonProperty("status") String status,
            @JsonProperty("bestelltAm") Timestamp bestelltAm) {
        this.bestellungId = bestellungId;
        this.userEmail = userEmail;
        this.adressId = adressId;
        this.gesamtpreis = gesamtpreis;
        this.status = status;
        this.bestelltAm = bestelltAm;
    }

    public Integer getBestellungId() {
        return bestellungId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getAdressId() {
        return adressId;
    }

    public BigDecimal getGesamtpreis() {
        return gesamtpreis;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getBestelltAm() {
        return bestelltAm;
    }
}
