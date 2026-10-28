package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "artikelId", nullable = false)
    private Integer artikelId;

    protected WarenkorbEintragEntity() {
        // Required by JPA
    }

    public WarenkorbEintragEntity(
            String userEmail,
            Integer artikelId,
            Integer menge
            ) {

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("userEmail darf nicht null/leer sein");
        }
        if (artikelId == null) {
            throw new IllegalArgumentException("artikelId darf nicht null sein");
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

    public Integer getMenge() {
        return menge;
    }

    public void erhoeheMenge(int menge) {
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein");
        }
        this.menge += menge;
    }

    public Integer getArtikelId() {
        return artikelId;
    }
}
