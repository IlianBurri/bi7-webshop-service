package ch.suva.bi7.webshop.service.dao;

public class LagerbestandException extends DaoException {

    private final int artikelId;

    public LagerbestandException(int artikelId, String message) {
        super(message);
        this.artikelId = artikelId;
    }

    public LagerbestandException(int artikelId, String message, Throwable cause) {
        super(message, cause);
        this.artikelId = artikelId;
    }

    public int getArtikelId() {
        return artikelId;
    }
}
