package ch.suva.bi7.webshop.service.db.entity;

import java.math.BigDecimal;

public class ArtikelEntity {

    private static final BigDecimal MINDESTPREIS = new BigDecimal("0.01");

    private Integer artikelId;
    private String name;
    private BigDecimal preis;
    private String bild;

    public ArtikelEntity(
            Integer artikelId,
            String name,
            BigDecimal preis,
            String bild) {
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
