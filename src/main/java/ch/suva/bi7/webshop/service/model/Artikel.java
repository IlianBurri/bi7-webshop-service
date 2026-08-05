package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class Artikel {
    public final Integer artikelId;
    public final String name;
    public final BigDecimal preis;
    public final String bild;

    public Artikel(
            @JsonProperty("artikelId") Integer artikelId,
            @JsonProperty("name") String name,
            @JsonProperty("preis") BigDecimal preis,
            @JsonProperty("bild") String bild) {
        this.artikelId = artikelId;
        this.name = name;
        this.preis = preis;
        this.bild = bild;
    }
}
