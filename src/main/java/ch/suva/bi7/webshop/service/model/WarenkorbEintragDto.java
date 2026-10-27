package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class WarenkorbEintragDto {

    private final Integer warenkorbItemId;
    private final Integer artikelId;
    // Zusätzlich zur 'artikelId' auch artikelDto hier ablegen.
    private final Integer menge;
    // TODO Statt artikelName, artikelPreis, artikelBild einfach ArtikelDto verwenden, wenn WarenkorbEintragEntity wieder auf FK 'artikelId' umgestellt ist.
    private final String artikelName;
    private final BigDecimal artikelPreis;
    private final String artikelBild;
    // TODO Neu das hier verwenden:
    //private final ArtikelDto artikelDto;

    public WarenkorbEintragDto(
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
        // TODO Umstellen auf artikelDto.getName(), wenn WarenkorbEintragEntity wieder auf FK 'artikelId' umgestellt ist.
        return artikelName;
    }

    public BigDecimal getArtikelPreis() {
        // TODO Umstellen auf artikelDto.getPreis(), wenn WarenkorbEintragEntity wieder auf FK 'artikelId' umgestellt ist.
        return artikelPreis;
    }

    public String getArtikelBild() {
        // TODO Umstellen auf artikelDto.getBild(), wenn WarenkorbEintragEntity wieder auf FK 'artikelId' umgestellt ist.
        return artikelBild;
    }
}
