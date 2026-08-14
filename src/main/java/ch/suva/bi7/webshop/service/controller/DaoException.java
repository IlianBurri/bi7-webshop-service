package ch.suva.bi7.webshop.service.controller;

/**
 * Einheitliche, präzise Exception für Fehler beim Datenbankzugriff.
 *
 * Ersetzt das zu breite {@code throws Exception} der DAO-Schnittstellen:
 * Ein Controller kann gezielt auf {@code DaoException} reagieren (z. B. mit
 * HTTP 500), ohne gleich jede beliebige Exception abfangen zu müssen.
 */
public class DaoException extends Exception {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
