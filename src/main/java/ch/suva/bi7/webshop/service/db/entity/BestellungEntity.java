package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "bestellung")
public class BestellungEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bestellungId", nullable = false)
    private Integer bestellungId;

    @Column(name = "userEmail", nullable = false, length = 150)
    private String userEmail;

    @Column(name = "adressId", nullable = false)
    private Integer adressId;

    @Column(name = "gesamtpreis", nullable = false, precision = 10, scale = 2)
    private BigDecimal gesamtpreis;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "bestelldatum")
    private Timestamp bestelltAm;

    protected BestellungEntity() {
        // Required by JPA
    }

    public BestellungEntity(Integer bestellungId,
                            String userEmail,
                            Integer adressId,
                            BigDecimal gesamtpreis,
                            String status,
                            Timestamp bestelltAm) {
        if (bestellungId != null && bestellungId <= 0) {
            throw new IllegalArgumentException(
                    "bestellungId muss > 0 sein, wenn sie bereits vergeben ist");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "userEmail darf nicht null/leer sein");
        }
        if (userEmail.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "userEmail darf maximal 150 Zeichen lang sein");
        }
        if (adressId == null || adressId <= 0) {
            throw new IllegalArgumentException("adressId muss > 0 sein");
        }
        if (gesamtpreis == null || gesamtpreis.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("gesamtpreis muss > 0 sein");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "status darf nicht null/leer sein");
        }
        if (status.trim().length() > 50) {
            throw new IllegalArgumentException(
                    "status darf maximal 50 Zeichen lang sein");
        }

        this.bestellungId = bestellungId;
        this.userEmail = userEmail.trim();
        this.adressId = adressId;
        this.gesamtpreis = gesamtpreis;
        this.status = status.trim();
        this.bestelltAm = bestelltAm;
    }

    public Integer getBestellungId() {
        return bestellungId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public Integer getAdressId() {
        return adressId;
    }

    public BigDecimal getGesamtpreis() {
        return gesamtpreis;
    }

    public String getStatus() {
        return status;
    }

    public Timestamp getBestelltAm() {
        return bestelltAm;
    }
}
