package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.dao.DaoException;
import ch.suva.bi7.webshop.service.dao.LagerbestandException;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.mapper.WarenkorbEintragMapper;
import ch.suva.bi7.webshop.service.model.VerfuegbarkeitResponse;
import ch.suva.bi7.webshop.service.model.VerfuegbarkeitsProblemDto;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarenkorbService {

    private final WarenkorbDao warenkorbDao;
    private final ArtikelDao artikelDao;

    public WarenkorbService(WarenkorbDao warenkorbDao, ArtikelDao artikelDao) {
        if (warenkorbDao == null) {
            throw new IllegalArgumentException(
                    "warenkorbDao must not be null");
        }
        if (artikelDao == null) {
            throw new IllegalArgumentException(
                    "artikelDao must not be null");
        }
        this.warenkorbDao = warenkorbDao;
        this.artikelDao = artikelDao;
    }

    public List<WarenkorbEintragDto> ladeWarenkorb(String email)
            throws DaoException {
        pruefeEmail(email);
        // Schritt 1: WarenkorbEintragEntity aus der Datenbank laden mit 'artikelId' als Fremdschlüssel aber artikelDto ersteinmal 'null' setzen.
        return warenkorbDao.getWarenkorbNachBenutzer(email)
                .stream()
                .map(WarenkorbEintragMapper::toDto)
                .toList();
    }

    public void fuegeArtikelHinzu(
            String email,
            Integer artikelId,
            int menge
    ) throws DaoException {
        pruefeEmail(email);
        if (artikelId == null) {
            throw new IllegalArgumentException("artikel darf nicht null sein.");
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein.");
        }

        ArtikelEntity artikel = ladeArtikel(artikelId);
        int bereitsImWarenkorb = warenkorbDao.getWarenkorbNachBenutzer(email).stream()
                .filter(eintrag -> artikelId.equals(eintrag.getArtikelId()))
                .mapToInt(WarenkorbEintragEntity::getMenge)
                .sum();
        if (bereitsImWarenkorb + menge > artikel.getLagerbestand()) {
            throw new LagerbestandException(artikelId, lagerbestandMeldung(artikel, bereitsImWarenkorb));
        }

        warenkorbDao.fuegeArtikelZuWarenkorbHinzu(email, artikelId, menge);
    }

    public boolean aktualisiereMenge(
            int warenkorbItemId,
            int menge
    ) throws DaoException {
        if (warenkorbItemId <= 0) {
            throw new IllegalArgumentException("ID muss > 0 sein.");
        }
        if (menge <= 0) {
            throw new IllegalArgumentException("menge muss > 0 sein.");
        }

        Optional<WarenkorbEintragEntity> eintrag = warenkorbDao.getWarenkorbEintragById(warenkorbItemId);
        if (eintrag.isEmpty()) {
            return false;
        }
        // Reduzieren ist immer erlaubt, auch wenn der Bestand inzwischen kleiner ist als die Menge im Warenkorb.
        boolean erhoeht = menge > eintrag.get().getMenge();
        ArtikelEntity artikel = ladeArtikel(eintrag.get().getArtikelId());
        if (erhoeht && menge > artikel.getLagerbestand()) {
            throw new LagerbestandException(artikel.getArtikelId(), lagerbestandMeldung(artikel, 0));
        }

        return warenkorbDao.aktualisiereMenge(warenkorbItemId, menge);
    }

    public boolean loescheWarenkorbEintrag(int warenkorbItemId)
            throws DaoException {
        if (warenkorbItemId <= 0) {
            throw new IllegalArgumentException("ID muss > 0 sein.");
        }
        return warenkorbDao.loescheWarenkorbEintrag(warenkorbItemId);
    }

    public boolean leereWarenkorb(String email) throws DaoException {
        pruefeEmail(email);
        return warenkorbDao.leereWarenkorbNachBenutzer(email);
    }

    public VerfuegbarkeitResponse pruefeVerfuegbarkeit(List<WarenkorbEintragDto> warenkorbMitArtikeln) {
        if (warenkorbMitArtikeln == null) {
            throw new IllegalArgumentException("Warenkorb darf nicht null sein.");
        }

        List<VerfuegbarkeitsProblemDto> probleme = new ArrayList<>();
        for (WarenkorbEintragDto eintrag : warenkorbMitArtikeln) {
            int menge = eintrag.getMenge() == null ? 0 : eintrag.getMenge();
            // null = Artikel existiert nicht mehr -> gilt wie ausverkauft
            int lagerbestand = eintrag.getArtikelLagerbestand() == null ? 0 : eintrag.getArtikelLagerbestand();
            if (menge > lagerbestand) {
                probleme.add(new VerfuegbarkeitsProblemDto(eintrag.getWarenkorbItemId(), eintrag.getArtikelId()));
            }
        }
        return new VerfuegbarkeitResponse(probleme);
    }

    private ArtikelEntity ladeArtikel(Integer artikelId) throws DaoException {
        ArtikelEntity artikel;
        try {
            artikel = artikelDao.getArtikelById(artikelId);
        } catch (Exception e) {
            throw new DaoException("Artikel " + artikelId + " konnte nicht geladen werden.", e);
        }
        if (artikel == null) {
            throw new IllegalArgumentException("Artikel " + artikelId + " nicht gefunden.");
        }
        return artikel;
    }

    private String lagerbestandMeldung(ArtikelEntity artikel, int bereitsImWarenkorb) {
        if (artikel.getLagerbestand() == 0) {
            return "\"" + artikel.getName() + "\" ist ausverkauft.";
        }
        String meldung = "Von \"" + artikel.getName() + "\" sind nur noch "
                + artikel.getLagerbestand() + " Stück verfügbar.";
        if (bereitsImWarenkorb > 0) {
            meldung += " Davon hast du bereits " + bereitsImWarenkorb + " im Warenkorb.";
        }
        return meldung;
    }

    private void pruefeEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Parameter 'email' erforderlich.");
        }
    }
}
