package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Adresse;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdresseControllerTest {

    private static final String TEST_EMAIL = "max@example.ch";

    private static final Adresse BEISPIEL_ADRESSE =
            new Adresse(0, TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");

    @Test
    void adressenAbrufenLiefertAdressenAlsJson() throws Exception {
        Adresse adresse = new Adresse(1, TEST_EMAIL, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");
        AdresseContextMock ctx = new AdresseContextMock();
        ctx.setPathParam("email", TEST_EMAIL);
        AdresseController controller = new AdresseController(new FakeAdresseDao(List.of(adresse)));

        controller.getAdressen.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        List<?> json = (List<?>) ctx.gesendetesJson;
        assertEquals(1, json.size());
        Adresse zurueckgegeben = (Adresse) json.get(0);
        assertEquals(1, zurueckgegeben.adressId());
        assertEquals("Schweiz", zurueckgegeben.land());

        AdresseContextMock leererCtx = new AdresseContextMock();
        leererCtx.setPathParam("email", TEST_EMAIL);
        AdresseController leererController = new AdresseController(new FakeAdresseDao(Collections.emptyList()));

        leererController.getAdressen.handle(leererCtx);

        assertEquals(200, leererCtx.gesetzterStatus);
        assertTrue(((List<?>) leererCtx.gesendetesJson).isEmpty(), "Ohne Adressen muss ein leeres JSON kommen");
    }

    @Test
    void adresseAnlegenTrimmtFelderUndSetztDefaultLand() throws Exception {
        FakeAdresseDao dao = new FakeAdresseDao(Collections.emptyList());
        Adresse mitLeerzeichen = new Adresse(0, "  " + TEST_EMAIL + "  ", "  Max  ", " Muster ", " Musterstrasse 1 ",
                " 8000 ", " Zuerich ", "  ");
        AdresseContextMock ctx = new AdresseContextMock(mitLeerzeichen);
        AdresseController controller = new AdresseController(dao);

        controller.createAdresse.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus);
        Adresse gespeichert = dao.gespeicherteAdresse;
        assertNotNull(gespeichert, "DAO.insert muss aufgerufen werden");
        assertEquals(TEST_EMAIL, gespeichert.userEmail(), "userEmail muss getrimmt werden");
        assertEquals("Zuerich", gespeichert.ort());
        assertEquals("Schweiz", gespeichert.land(), "Ohne land muss der Default Schweiz verwendet werden");
        assertEquals(42, ((Adresse) ctx.gesendetesJson).adressId(), "Die vergebene adressId muss im JSON stehen");
    }

    @Test
    void adresseAktualisierenGibtErfolgreichZurueck() throws Exception {
        FakeAdresseDao dao = new FakeAdresseDao(Collections.emptyList());
        AdresseContextMock updateCtx = new AdresseContextMock(BEISPIEL_ADRESSE);
        updateCtx.setPathParam("adressId", "7");
        AdresseController controller = new AdresseController(dao);

        controller.updateAdresse.handle(updateCtx);

        assertEquals(200, updateCtx.gesetzterStatus);
        assertEquals(7, dao.updateId);
        assertEquals(TEST_EMAIL, dao.updateAdresse.userEmail());
    }

    @Test
    void adresseLoeschenLoeschtAdresse() throws Exception {
        FakeAdresseDao dao = new FakeAdresseDao(Collections.emptyList());
        AdresseContextMock deleteCtx = new AdresseContextMock();
        deleteCtx.setPathParam("adressId", "7");
        AdresseController controller = new AdresseController(dao);

        controller.deleteAdresse.handle(deleteCtx);

        assertEquals(200, deleteCtx.gesetzterStatus);
        assertEquals(7, dao.deleteId);
    }

    @Test
    void adresseAnlegenMitFehlendemPflichtfeldLiefert400() throws Exception {
        FakeAdresseDao daoOhneEmail = new FakeAdresseDao(Collections.emptyList());
        Adresse ohneEmail = new Adresse(0, null, "Max", "Muster", "Musterstrasse 1", "8000", "Zuerich", "Schweiz");
        AdresseContextMock fehlerCtx = new AdresseContextMock(ohneEmail);
        AdresseController controller = new AdresseController(daoOhneEmail);

        controller.createAdresse.handle(fehlerCtx);

        assertEquals(400, fehlerCtx.gesetzterStatus);
        assertNull(daoOhneEmail.gespeicherteAdresse, "DAO darf bei Validierungsfehler nicht aufgerufen werden");
        assertEquals("'userEmail' ist ein Pflichtfeld und darf nicht leer sein.", fehlermeldung(fehlerCtx));
    }

    @Test
    void adressenAbrufenBeiDatenbankFehlerLiefert500MitFehlermeldung() throws Exception {
        AdresseContextMock dbFehlerCtx = new AdresseContextMock();
        dbFehlerCtx.setPathParam("email", TEST_EMAIL);
        AdresseController controller = new AdresseController(new FehlerAdresseDao());

        controller.getAdressen.handle(dbFehlerCtx);

        assertEquals(500, dbFehlerCtx.gesetzterStatus);
        assertEquals("Fehler beim Abrufen der Adressen.", fehlermeldung(dbFehlerCtx),
                "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
    }

    @Test
    void adresseAnlegenBeiIdentischerAdresseLiefert200UndInsertiertNicht() throws Exception {
        FakeAdresseDao dao = new FakeAdresseDao(Collections.emptyList());
        dao.existsIdenticalErgebnis = true;
        AdresseContextMock ctx = new AdresseContextMock(BEISPIEL_ADRESSE);
        AdresseController controller = new AdresseController(dao);

        controller.createAdresse.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus, "Duplikat muss mit 200 statt 201 beantwortet werden");
        assertNull(dao.gespeicherteAdresse, "Bei bereits existierender Adresse darf kein INSERT ausgeführt werden");
    }

    @Test
    void adresseAktualisierenBeiFehlendemDatensatzLiefert404() throws Exception {
        FakeAdresseDao dao = new FakeAdresseDao(Collections.emptyList());
        dao.updateErgebnis = false;
        AdresseContextMock ctx = new AdresseContextMock(BEISPIEL_ADRESSE);
        ctx.setPathParam("adressId", "7");
        AdresseController controller = new AdresseController(dao);

        controller.updateAdresse.handle(ctx);

        assertEquals(404, ctx.gesetzterStatus);
        assertEquals("Adresse nicht gefunden", fehlermeldung(ctx));
    }

    @Test
    void adresseLoeschenBeiFehlendemDatensatzLiefert404() throws Exception {
        FakeAdresseDao dao = new FakeAdresseDao(Collections.emptyList());
        dao.deleteErgebnis = false;
        AdresseContextMock ctx = new AdresseContextMock();
        ctx.setPathParam("adressId", "7");
        AdresseController controller = new AdresseController(dao);

        controller.deleteAdresse.handle(ctx);

        assertEquals(404, ctx.gesetzterStatus);
        assertEquals("Adresse nicht gefunden", fehlermeldung(ctx));
    }

    @Test
    void adresseAktualisierenMitUngueltigerAdressIdLiefert400() throws Exception {
        AdresseContextMock ctx = new AdresseContextMock(BEISPIEL_ADRESSE);
        ctx.setPathParam("adressId", "abc");
        AdresseController controller = new AdresseController(new FakeAdresseDao(Collections.emptyList()));

        controller.updateAdresse.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
        assertEquals("adressId muss eine Zahl sein.", fehlermeldung(ctx));
    }

    @Test
    void adresseLoeschenMitUngueltigerAdressIdLiefert400() throws Exception {
        AdresseContextMock ctx = new AdresseContextMock();
        ctx.setPathParam("adressId", "abc");
        AdresseController controller = new AdresseController(new FakeAdresseDao(Collections.emptyList()));

        controller.deleteAdresse.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
        assertEquals("adressId muss eine Zahl sein.", fehlermeldung(ctx));
    }

    @Test
    void adresseAnlegenMitUngueltigemJsonBodyLiefert400() throws Exception {
        AdresseContextMock ctx = new AdresseContextMock();
        ctx.jsonFehler = true;
        AdresseController controller = new AdresseController(new FakeAdresseDao(Collections.emptyList()));

        controller.createAdresse.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
        assertEquals("Ungültiger JSON-Request-Body.", fehlermeldung(ctx));
    }

    @SuppressWarnings("unchecked")
    private static String fehlermeldung(AdresseContextMock ctx) {
        Map<String, Object> fehler = (Map<String, Object>) ctx.gesendetesJson;
        assertNotNull(fehler, "Es muss ein JSON-Fehlerobjekt gesendet werden");
        return (String) fehler.get("error");
    }
}

class FakeAdresseDao implements AdresseDao {

    private final List<Adresse> adressen;

    boolean existsIdenticalErgebnis = false;
    boolean updateErgebnis = true;
    boolean deleteErgebnis = true;

    Adresse gespeicherteAdresse;
    Integer updateId;
    Adresse updateAdresse;
    Integer deleteId;

    FakeAdresseDao(List<Adresse> adressen) {
        this.adressen = adressen;
    }

    @Override
    public List<Adresse> findByUserEmail(String email) {
        return adressen;
    }

    @Override
    public Adresse insert(Adresse adresse) {
        this.gespeicherteAdresse = adresse;
        // simuliert die von der DB vergebene adressId
        return new Adresse(42, adresse.userEmail(), adresse.vorname(), adresse.nachname(),
                adresse.strasse(), adresse.plz(), adresse.ort(), adresse.land());
    }

    @Override
    public boolean update(int adressId, Adresse adresse) {
        this.updateId = adressId;
        this.updateAdresse = adresse;
        return updateErgebnis;
    }

    @Override
    public boolean delete(int adressId) {
        this.deleteId = adressId;
        return deleteErgebnis;
    }

    @Override
    public boolean existsIdentical(Adresse adresse) {
        return existsIdenticalErgebnis;
    }
}

class FehlerAdresseDao implements AdresseDao {

    @Override
    public List<Adresse> findByUserEmail(String email) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public Adresse insert(Adresse adresse) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean update(int adressId, Adresse adresse) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean delete(int adressId) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean existsIdentical(Adresse adresse) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }
}

class AdresseContextMock extends EinfacherContextMock {

    private final Map<String, String> pathParams = new HashMap<>();
    String gesendetesResult;
    boolean jsonFehler = false;

    AdresseContextMock() {
        super(null);
    }

    AdresseContextMock(Object vorgegebenerBody) {
        super(vorgegebenerBody);
    }

    void setPathParam(String key, String value) {
        pathParams.put(key, value);
    }

    @Override
    public String pathParam(String key) {
        return pathParams.getOrDefault(key, "");
    }

    @Override
    public <T> T bodyAsClass(Class<T> clazz) {
        if (jsonFehler) {
            throw new BadRequestResponse("Invalid body");
        }
        return super.bodyAsClass(clazz);
    }

    @Override
    public Context result(String result) {
        this.gesendetesResult = result;
        return this;
    }
}
