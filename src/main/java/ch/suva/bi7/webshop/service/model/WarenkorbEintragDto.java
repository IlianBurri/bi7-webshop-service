package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class WarenkorbEintragDto {

    private final Integer warenkorbItemId;
    private final Integer artikelId;
    private final Integer menge;
    private ArtikelDto artikelDto;

    public WarenkorbEintragDto(
            @JsonProperty("warenkorbItemId") Integer warenkorbItemId,
            @JsonProperty("artikelId") Integer artikelId,
            @JsonProperty("menge") Integer menge) {
        this.warenkorbItemId = warenkorbItemId;
        this.artikelId = artikelId;
        this.menge = menge;
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

    public void setArtikelDto(ArtikelDto artikelDto) {
        this.artikelDto = artikelDto;
    }

    public String getArtikelName() {
        return artikelDto == null ? null : artikelDto.getName();
    }

    public BigDecimal getArtikelPreis() {
        return artikelDto == null ? null : artikelDto.getPreis();
    }

    public String getArtikelBild() {
        return artikelDto == null ? null : artikelDto.getBild();
    }

    public Integer getArtikelLagerbestand() {
        return artikelDto == null ? null : artikelDto.getLagerbestand();
    }
}
