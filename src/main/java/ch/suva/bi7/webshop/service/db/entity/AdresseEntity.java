package ch.suva.bi7.webshop.service.db.entity;

public class AdresseEntity {
    private final Integer adressId;
    private final String userEmail;
    private final String vorname;
    private final String nachname;
    private final String strasse;
    private final String plz;
    private final String ort;
    private final String land;

    public AdresseEntity(Integer adressId,
                         String userEmail,
                         String vorname,
                         String nachname,
                         String strasse,
                         String plz,
                         String ort,
                         String land) {

        if (adressId != null && adressId <= 0) {
            throw new IllegalArgumentException("adressId muss > 0 sein, wenn sie bereits vergeben ist");
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("userEmail darf nicht null/leer sein");
        }
        if (userEmail.length() > 150) {
            throw new IllegalArgumentException("userEmail darf maximal 150 Zeichen lang sein");
        }

        if (vorname == null || vorname.trim().isEmpty()) {
            throw new IllegalArgumentException("Vorname darf nicht null/leer sein");
        }
        if (vorname.length() > 100) {
            throw new IllegalArgumentException("Vorname darf maximal 100 Zeichen lang sein");
        }

        if (nachname == null || nachname.trim().isEmpty()) {
            throw new IllegalArgumentException("Nachname darf nicht null/leer sein");
        }
        if (nachname.length() > 100) {
            throw new IllegalArgumentException("Nachname darf maximal 100 Zeichen lang sein");
        }

        if (strasse == null || strasse.trim().isEmpty()) {
            throw new IllegalArgumentException("Strasse darf nicht null/leer sein");
        }
        if (strasse.length() > 200) {
            throw new IllegalArgumentException("Strassename darf maximal 200 Zeichen lang sein");
        }

        if (plz == null || plz.trim().isEmpty()) {
            throw new IllegalArgumentException("Plz darf nicht null/leer sein");
        }
        if (plz.length() > 20) {
            throw new IllegalArgumentException("Plz darf maximal 20 Zeichen lang sein");
        }

        if (ort == null || ort.trim().isEmpty()) {
            throw new IllegalArgumentException("Ort darf nicht null/leer sein");
        }
        if (ort.length() > 100) {
            throw new IllegalArgumentException("Ortname darf maximal 100 Zeichen lang sein");
        }

        if (land == null || land.trim().isEmpty()) {
            throw new IllegalArgumentException("Land darf nicht null/leer sein");
        }
        if (land.length() > 100) {
            throw new IllegalArgumentException("Land darf maximal 100 Zeichen lang sein");
        }

        this.adressId = adressId;
        this.userEmail = userEmail;
        this.vorname = vorname;
        this.nachname = nachname;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.land = land;
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