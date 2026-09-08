package ch.suva.bi7.webshop.service.db.entity;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BestellungEntity {

    private final Integer bestellungId;
    private final String userEmail;
    private final Integer adressId;
    private final BigDecimal gesamtpreis;
    private final String status;
    private final Timestamp bestelltAm;


    public BestellungEntity(Integer bestellungId,
                            String userEmail,
                            Integer adressId,
                            BigDecimal gesamtpreis,
                            String status,
                            Timestamp bestelltAm) {

        if (bestellungId == null || bestellungId <= 0) {
            throw new IllegalArgumentException("bestellungId muss > 0 sein");
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("userEmail darf nicht null/leer sein");
        }

        if (userEmail.length() > 150) {
            throw new IllegalArgumentException("userEmail darf maximal 150 Zeichen lang sein");
        }

        if (adressId == null || adressId <= 0) {
            throw new IllegalArgumentException("adressId muss > 0 sein");
        }

        if (gesamtpreis == null || gesamtpreis.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("gesamtpreis muss > 0 sein");
        }

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("status darf nicht null/leer sein");
        }

        if (status.length() > 50) {
            throw new IllegalArgumentException("status darf maximal 50 Zeichen lang sein");
        }

        if (bestelltAm == null) {
            throw new IllegalArgumentException("bestelltAm darf nicht null sein");
        }


        this.bestellungId = bestellungId;
        this.userEmail = userEmail;
        this.adressId = adressId;
        this.gesamtpreis = gesamtpreis;
        this.status = status;
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
