package ch.suva.bi7.webshop.service.db.entity;

import java.math.BigDecimal;

public class WarenkorbEintragEntity {
    private final Integer warenkorbItemId;
    private final String userEmail;
    private final Integer artikelId;
    private final Integer menge;
    private final String artikelName;
    private final BigDecimal artikelPreis;
    private final String artikelBild;


    public WarenkorbEintragEntity(
            Integer warenkorbItemId,
            String userEmail,
            Integer artikelId,
            Integer menge,
            String artikelName,
            BigDecimal artikelPreis,
            String artikelBild) {

        if (warenkorbItemId != null && warenkorbItemId <= 0) {
            throw new IllegalArgumentException("warenkorbItemId muss > 0 sein");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("userEmail darf nicht null/leer sein");
        }
        if (artikelId == null || artikelId <= 0) {
            throw new IllegalArgumentException("artikelId muss > 0 sein");
        }
        if (menge == null || menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein");
        }
        if (artikelName == null || artikelName.trim().isEmpty()) {
            throw new IllegalArgumentException("artikelName darf nicht null/leer sein");
        }
        if (artikelPreis == null || artikelPreis.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("artikelPreis muss > 0 sein");
        }
        if (artikelBild != null && artikelBild.trim().isEmpty()) {
            throw new IllegalArgumentException("artikelBild darf nicht leer sein");
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
