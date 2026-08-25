package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class AddArtikelRequest {
    public final String name;
    public final BigDecimal preis;
    public final String bild;

    public AddArtikelRequest(
            @JsonProperty("name") String name,
            @JsonProperty("preis") BigDecimal preis,
            @JsonProperty("bild") String bild) {
        if (name == null || preis == null) {
            throw new IllegalArgumentException("name and preis must not be null");
        }
        this.name = name;
        this.preis = preis;
        this.bild = bild;
    }
}
