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
        // TODO: artikelId == null testen (oder bewusst dokumentieren, dass erlaubt)

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht null/leer sein");
        }
        if (preis == null || preis.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preis darf nicht null/negativ sein");
        }
        if (preis == null || preis.compareTo(BigDecimal.ZERO) < 0) {
          throw new IllegalArgumentException("Preis darf nicht negativ sein");
       }
        this.artikelId = artikelId;
        this.name = name;
        this.preis = preis;
        this.bild = bild;
    }
}
