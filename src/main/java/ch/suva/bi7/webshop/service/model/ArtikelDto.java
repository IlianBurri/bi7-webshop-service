package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ArtikelDto {

    private Integer artikelId;
    private String name;
    private BigDecimal preis;
    private String bild;

    public ArtikelDto(
            @JsonProperty("artikelId") Integer artikelId,
            @JsonProperty("name") String name,
            @JsonProperty("preis") BigDecimal preis,
            @JsonProperty("bild") String bild) {
        this.artikelId = artikelId;
        this.name = name.trim();
        this.preis = preis;
        this.bild = bild == null ? null : bild.trim();
    }

    public Integer getArtikelId() {
        return artikelId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPreis() {
        return preis;
    }

    public String getBild() {
        return bild;
    }
}
