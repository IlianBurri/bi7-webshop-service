package ch.suva.bi7.webshop.service.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class WarenkorbItem {
    public final Integer warenkorbItemId;
    public final String userEmail;
    public final Integer artikelId;
    public final Integer menge;
    public final String artikelName;
    public final BigDecimal artikelPreis;
    public final String artikelBild;

    public WarenkorbItem(
            @JsonProperty("warenkorbItemId") Integer warenkorbItemId,
            @JsonProperty("userEmail") String userEmail,
            @JsonProperty("artikelId") Integer artikelId,
            @JsonProperty("menge") Integer menge,
            @JsonProperty("artikelName") String artikelName,
            @JsonProperty("artikelPreis") BigDecimal artikelPreis,
            @JsonProperty("artikelBild") String artikelBild) {

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("userEmail darf nicht null/leer sein");
        }
        if (artikelId == null || artikelId <= 0) {
            throw new IllegalArgumentException("artikelId muss > 0 sein");
        }
        if (menge == null || menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein");
        }

        this.warenkorbItemId = warenkorbItemId;
        this.userEmail = userEmail;
        this.artikelId = artikelId;
        this.menge = menge;
        this.artikelName = artikelName;
        this.artikelPreis = artikelPreis;
        this.artikelBild = artikelBild;
    }

    public Integer getWarenkorbItemId() {
        return warenkorbItemId;
    }

    public String getUserEmail() {
        return userEmail;
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
