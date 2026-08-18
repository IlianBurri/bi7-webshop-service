package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class Artikel {

    private static final BigDecimal MINDESTPREIS = new BigDecimal("0.01");

    public final Integer artikelId;
    public final String name;
    public final BigDecimal preis;
    public final String bild;

    public Artikel(
            @JsonProperty("artikelId") Integer artikelId,
            @JsonProperty("name") String name,
            @JsonProperty("preis") BigDecimal preis,
            @JsonProperty("bild") String bild) {
        if (artikelId == null) {
            throw new IllegalArgumentException("artikelId darf nicht null sein");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht null/leer sein");
        }
        if (preis == null || preis.compareTo(MINDESTPREIS) < 0) {
            throw new IllegalArgumentException("Preis darf nicht null sein und muss mindestens 0.01 betragen");
        }
        if (bild != null && bild.trim().isEmpty()) {
            throw new IllegalArgumentException("Bild darf nicht leer sein (weglassen, wenn kein Bild vorhanden)");
        }

        this.artikelId = artikelId;
        this.name = name.trim();
        this.preis = preis;
        this.bild = bild == null ? null : bild.trim();
    }
}
