package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "warenkorb_item")
public class WarenkorbEintragEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warenkorbItemId", nullable = false)
    private Integer warenkorbItemId;

    @Column(name = "userEmail", nullable = false, length = 150)
    private String userEmail;

    @Column(name = "menge", nullable = false)
    private Integer menge;

    // TODO Wieder umstellen auf FK 'artikelId' und ManyToOne + ArtikelEntity löschen.
    // @Column(name = "artikelId", nullable = false)
    // private Integer artikelId;

    @ManyToOne
    @JoinColumn(name = "artikelId", insertable = true, updatable = true)
    private ArtikelEntity artikel;

    protected WarenkorbEintragEntity() {
        // Required by JPA
    }

    public WarenkorbEintragEntity(
            String userEmail,
            ArtikelEntity artikel,
            Integer menge
            ) {

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("userEmail darf nicht null/leer sein");
        }
        if (artikel == null) {
            throw new IllegalArgumentException("artikel darf nicht null sein");
        }
        if (menge == null || menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein");
        }

        this.userEmail = userEmail;
        this.artikel = artikel;
        this.menge = menge;
    }

    public Integer getWarenkorbItemId() {
        return warenkorbItemId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getMenge() {
        return menge;
    }

    public void erhoeheMenge(int menge) {
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein");
        }
        this.menge += menge;
    }

    // TODO Entfernen:
    public String getArtikelName() {
        return artikel.getName();
    }

    // TODO Entfernen:
    public BigDecimal getArtikelPreis() {
        return artikel.getPreis();
    }

    // TODO Entfernen:
    public String getArtikelBild() {
        return artikel.getBild();
    }

    public Integer getArtikelId() { return artikel.getArtikelId();}
}
