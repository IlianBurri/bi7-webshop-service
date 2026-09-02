package ch.suva.bi7.webshop.service.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Bestellung {

    private int bestellungId;
    private String userEmail;
    private int adressId;
    private BigDecimal gesamtpreis;
    private String status;
    private Timestamp bestelltAm;

    public Bestellung() {
    }

    public Bestellung(int bestellungId, String userEmail, int adressId, BigDecimal gesamtpreis, String status, Timestamp bestelltAm) {
        this.bestellungId = bestellungId;
        this.userEmail = userEmail;
        this.adressId = adressId;
        this.gesamtpreis = gesamtpreis;
        this.status = status;
        this.bestelltAm = bestelltAm;
    }

    public int getBestellungId() {
        return bestellungId;
    }

    public void setBestellungId(int bestellungId) {
        this.bestellungId = bestellungId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public int getAdressId() {
        return adressId;
    }

    public void setAdressId(int adressId) {
        this.adressId = adressId;
    }

    public BigDecimal getGesamtpreis() {
        return gesamtpreis;
    }

    public void setGesamtpreis(BigDecimal gesamtpreis) {
        this.gesamtpreis = gesamtpreis;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getBestelltAm() {
        return bestelltAm;
    }

    public void setBestelltAm(Timestamp bestelltAm) {
        this.bestelltAm = bestelltAm;
    }
}