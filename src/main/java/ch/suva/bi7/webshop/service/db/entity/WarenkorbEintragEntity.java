package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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

    @Column(name = "artikelId", nullable = false)
    private Integer artikelId;

    @Column(name = "menge", nullable = false)
    private Integer menge;

    @Transient
    private String artikelName;

    @Transient
    private BigDecimal artikelPreis;

    @Transient
    private String artikelBild;


    protected WarenkorbEintragEntity() {
        // Required by JPA
    }

    public WarenkorbEintragEntity(
            String userEmail,
            Integer artikelId,
            Integer menge,
            String artikelName,
            BigDecimal artikelPreis,
            String artikelBild) {

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

        this.userEmail = userEmail;
        this.artikelId = artikelId;
        this.menge = menge;
        this.artikelName = artikelName;
        this.artikelPreis = artikelPreis;
        this.artikelBild = artikelBild;
    }

    public WarenkorbEintragEntity(String userEmail, Integer artikelId, Integer menge) {
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("userEmail darf nicht null/leer sein");
        }
        if (artikelId == null || artikelId <= 0) {
            throw new IllegalArgumentException("artikelId muss > 0 sein");
        }
        if (menge == null || menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein");
        }
        this.userEmail = userEmail;
        this.artikelId = artikelId;
        this.menge = menge;
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

    public void erhoeheMenge(int menge) {
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein");
        }
        this.menge += menge;
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
