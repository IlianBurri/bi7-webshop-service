package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "bestellposition")
public class BestellPositionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bestellpositionId", nullable = false)
    private Integer bestellpositionId;

    @Column(name = "bestellungId", nullable = false)
    private Integer bestellungId;

    @Column(name = "artikelId", nullable = false)
    private Integer artikelId;

    @Column(name = "anzahl", nullable = false)
    private Integer anzahl;

    @Column(name = "einzelpreis", nullable = false, precision = 10, scale = 2)
    private BigDecimal einzelpreis;

    protected BestellPositionEntity() {
        // Required by JPA
    }

    public BestellPositionEntity(Integer bestellungId,
                                 Integer artikelId,
                                 Integer anzahl,
                                 BigDecimal einzelpreis) {
        if (bestellungId == null || bestellungId <= 0) {
            throw new IllegalArgumentException("bestellungId muss > 0 sein");
        }
        if (artikelId == null || artikelId <= 0) {
            throw new IllegalArgumentException("artikelId muss > 0 sein");
        }
        if (anzahl == null || anzahl <= 0) {
            throw new IllegalArgumentException("anzahl muss > 0 sein");
        }
        if (einzelpreis == null || einzelpreis.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("einzelpreis muss > 0 sein");
        }

        this.bestellungId = bestellungId;
        this.artikelId = artikelId;
        this.anzahl = anzahl;
        this.einzelpreis = einzelpreis;
    }

    public Integer getBestellpositionId() {
        return bestellpositionId;
    }

    public Integer getBestellungId() {
        return bestellungId;
    }

    public Integer getArtikelId() {
        return artikelId;
    }

    public Integer getAnzahl() {
        return anzahl;
    }

    public BigDecimal getEinzelpreis() {
        return einzelpreis;
    }
}
