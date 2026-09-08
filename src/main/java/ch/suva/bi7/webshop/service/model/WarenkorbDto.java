package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class WarenkorbDto {

    private final Integer warenkorbItemId;
    private final Integer artikelId;
    private final Integer menge;
    private final String artikelName;
    private final BigDecimal artikelPreis;
    private final String artikelBild;

    public WarenkorbDto(
            @JsonProperty("warenkorbItemId") Integer warenkorbItemId,
            @JsonProperty("artikelId") Integer artikelId,
            @JsonProperty("menge") Integer menge,
            @JsonProperty("artikelName") String artikelName,
            @JsonProperty("artikelPreis") BigDecimal artikelPreis,
            @JsonProperty("artikelBild") String artikelBild) {
        this.warenkorbItemId = warenkorbItemId;
        this.artikelId = artikelId;
        this.menge = menge;
        this.artikelName = artikelName;
        this.artikelPreis = artikelPreis;
        this.artikelBild = artikelBild;
    }

    public Integer getWarenkorbItemId() {
        return warenkorbItemId;
    }

    public Integer getArtikelId() {
        return artikelId;
    }

    public Integer getMenge() {
        return menge;
    }

    public String getArtikelName() {
        return artikelName;
    }

    public BigDecimal getArtikelPreis() {
        return artikelPreis;
    }

    public String getArtikelBild() {
        return artikelBild;
    }
}
