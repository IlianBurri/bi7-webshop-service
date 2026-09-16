package ch.suva.bi7.webshop.service.db.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "adresse")
public class AdresseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adressId", nullable = false)
    private Integer adressId;

    @Column(name = "userEmail", nullable = false, length = 150)
    private String userEmail;

    @Column(name = "vorname", nullable = false, length = 100)
    private String vorname;

    @Column(name = "nachname", nullable = false, length = 100)
    private String nachname;

    @Column(name = "strasse", nullable = false, length = 200)
    private String strasse;

    @Column(name = "plz", nullable = false, length = 20)
    private String plz;

    @Column(name = "ort", nullable = false, length = 100)
    private String ort;

    @Column(name = "land", nullable = false, length = 100)
    private String land;

    protected AdresseEntity() {
        // Required by JPA
    }

    public AdresseEntity(Integer adressId,
                         String userEmail,
                         String vorname,
                         String nachname,
                         String strasse,
                         String plz,
                         String ort,
                         String land) {
        if (adressId != null && adressId <= 0) {
            throw new IllegalArgumentException(
                    "adressId muss > 0 sein, wenn sie bereits vergeben ist");
        }
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "userEmail darf nicht null/leer sein");
        }
        if (userEmail.trim().length() > 150) {
            throw new IllegalArgumentException(
                    "userEmail darf maximal 150 Zeichen lang sein");
        }
        if (vorname == null || vorname.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Vorname darf nicht null/leer sein");
        }
        if (vorname.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Vorname darf maximal 100 Zeichen lang sein");
        }
        if (nachname == null || nachname.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Nachname darf nicht null/leer sein");
        }
        if (nachname.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Nachname darf maximal 100 Zeichen lang sein");
        }
        if (strasse == null || strasse.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Strasse darf nicht null/leer sein");
        }
        if (strasse.trim().length() > 200) {
            throw new IllegalArgumentException(
                    "Strassenname darf maximal 200 Zeichen lang sein");
        }
        if (plz == null || plz.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "PLZ darf nicht null/leer sein");
        }
        if (plz.trim().length() > 20) {
            throw new IllegalArgumentException(
                    "PLZ darf maximal 20 Zeichen lang sein");
        }
        if (ort == null || ort.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Ort darf nicht null/leer sein");
        }
        if (ort.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Ortsname darf maximal 100 Zeichen lang sein");
        }
        if (land == null || land.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Land darf nicht null/leer sein");
        }
        if (land.trim().length() > 100) {
            throw new IllegalArgumentException(
                    "Land darf maximal 100 Zeichen lang sein");
        }

        this.adressId = adressId;
        this.userEmail = userEmail.trim();
        this.vorname = vorname.trim();
        this.nachname = nachname.trim();
        this.strasse = strasse.trim();
        this.plz = plz.trim();
        this.ort = ort.trim();
        this.land = land.trim();
    }

    public Integer getAdressId() {
        return adressId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getVorname() {
        return vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public String getStrasse() {
        return strasse;
    }

    public String getPlz() {
        return plz;
    }

    public String getOrt() {
        return ort;
    }

    public String getLand() {
        return land;
    }
}
