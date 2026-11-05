package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "artikel")
public class ArtikelEntity {

    private static final BigDecimal MINDESTPREIS = new BigDecimal("0.01");
    public static final int STANDARD_LAGERBESTAND = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "artikelId", nullable = false)
    private Integer artikelId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "preis", nullable = false, precision = 10, scale = 2)
    private BigDecimal preis;

    @Column(name = "bild", length = 500)
    private String bild;

    @Column(name = "lagerbestand", nullable = false)
    private Integer lagerbestand;

    protected ArtikelEntity() {
        // Required by JPA
    }

    public ArtikelEntity(String name, BigDecimal preis, String bild) {
        this(name, preis, bild, STANDARD_LAGERBESTAND);
    }

    public ArtikelEntity(String name, BigDecimal preis, String bild, Integer lagerbestand) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name darf nicht null/leer sein");
        }
        if (preis == null || preis.compareTo(MINDESTPREIS) < 0) {
            throw new IllegalArgumentException("Preis darf nicht null sein und muss mindestens 0.01 betragen");
        }
        if (bild != null && bild.trim().isEmpty()) {
            throw new IllegalArgumentException("Bild darf nicht leer sein (weglassen, wenn kein Bild vorhanden)");
        }
        if (lagerbestand == null || lagerbestand < 0) {
            throw new IllegalArgumentException("Lagerbestand darf nicht null sein und muss mindestens 0 betragen");
        }

        this.artikelId = null;
        this.name = name.trim();
        this.preis = preis;
        this.bild = bild == null ? null : bild.trim();
        this.lagerbestand = lagerbestand;
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

    public Integer getLagerbestand() {
        return lagerbestand;
    }
}
