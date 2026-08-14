package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.WarenkorbItem;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbControllerTest {

    @Test
    void warenkorbAbrufenLiefertItemsAlsJson() throws Exception {
        WarenkorbItem item = new WarenkorbItem(
                1, "test@example.com", 5, 3,
                "iPhone 15 Pro", new BigDecimal("1199.00"), null);
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("email", "test@example.com");
        WarenkorbController controller = new WarenkorbController(new FakeWarenkorbDao(List.of(item)));

        controller.getWarenkorb.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        List<?> json = (List<?>) ctx.gesendetesJson;
        assertEquals(1, json.size());

        WarenkorbContextMock leererCtx = new WarenkorbContextMock();
        leererCtx.setPathParam("email", "test@example.com");
        WarenkorbController leererController = new WarenkorbController(new FakeWarenkorbDao(Collections.emptyList()));

        leererController.getWarenkorb.handle(leererCtx);

        assertEquals(200, leererCtx.gesetzterStatus);
        assertTrue(((List<?>) leererCtx.gesendetesJson).isEmpty(), "Ohne Items muss ein leeres JSON kommen");
    }

    @Test
    void artikelHinzufuegenUndMengeAktualisierenFunktionieren() throws Exception {
        FakeWarenkorbDao dao = new FakeWarenkorbDao(Collections.emptyList());
        WarenkorbContextMock addCtx = new WarenkorbContextMock();
        addCtx.setQueryParam("email", "test@example.com");
        addCtx.setQueryParam("artikelId", "5");
        addCtx.setQueryParam("menge", "3");
        WarenkorbController controller = new WarenkorbController(dao);

        controller.addToWarenkorb.handle(addCtx);

        assertEquals(201, addCtx.gesetzterStatus);
        assertEquals("test@example.com", dao.addEmail);
        assertEquals(5, dao.addArtikelId);
        assertEquals(3, dao.addMenge);

        WarenkorbContextMock updateCtx = new WarenkorbContextMock();
        updateCtx.setPathParam("id", "7");
        updateCtx.setQueryParam("menge", "5");

        controller.updateMenge.handle(updateCtx);

        assertEquals(200, updateCtx.gesetzterStatus);
        assertEquals(7, dao.updateId);
        assertEquals(5, dao.updateNeueMenge);
    }

    @Test
    void addToWarenkorbMitUngueltigenParameternLiefert400() throws Exception {
        WarenkorbController controller = new WarenkorbController(new FakeWarenkorbDao(Collections.emptyList()));

        // fehlende artikelId
        WarenkorbContextMock fehlendeArtikelId = new WarenkorbContextMock();
        fehlendeArtikelId.setQueryParam("email", "test@example.com");
        controller.addToWarenkorb.handle(fehlendeArtikelId);
        assertEquals(400, fehlendeArtikelId.gesetzterStatus);

        // menge = 0
        WarenkorbContextMock mengeNull = new WarenkorbContextMock();
        mengeNull.setQueryParam("email", "test@example.com");
        mengeNull.setQueryParam("artikelId", "5");
        mengeNull.setQueryParam("menge", "0");
        controller.addToWarenkorb.handle(mengeNull);
        assertEquals(400, mengeNull.gesetzterStatus);

        // negative menge
        WarenkorbContextMock negativeMenge = new WarenkorbContextMock();
        negativeMenge.setQueryParam("email", "test@example.com");
        negativeMenge.setQueryParam("artikelId", "5");
        negativeMenge.setQueryParam("menge", "-2");
        controller.addToWarenkorb.handle(negativeMenge);
        assertEquals(400, negativeMenge.gesetzterStatus);

        // artikelId keine Zahl
        WarenkorbContextMock artikelIdKeineZahl = new WarenkorbContextMock();
        artikelIdKeineZahl.setQueryParam("email", "test@example.com");
        artikelIdKeineZahl.setQueryParam("artikelId", "abc");
        controller.addToWarenkorb.handle(artikelIdKeineZahl);
        assertEquals(400, artikelIdKeineZahl.gesetzterStatus);

        // menge keine Zahl
        WarenkorbContextMock mengeKeineZahl = new WarenkorbContextMock();
        mengeKeineZahl.setQueryParam("email", "test@example.com");
        mengeKeineZahl.setQueryParam("artikelId", "5");
        mengeKeineZahl.setQueryParam("menge", "abc");
        controller.addToWarenkorb.handle(mengeKeineZahl);
        assertEquals(400, mengeKeineZahl.gesetzterStatus);
    }

    @Test
    void updateMengeMitUngueltigenParameternLiefert400() throws Exception {
        WarenkorbController controller = new WarenkorbController(new FakeWarenkorbDao(Collections.emptyList()));

        // menge fehlt
        WarenkorbContextMock mengeFehlt = new WarenkorbContextMock();
        mengeFehlt.setPathParam("id", "7");
        controller.updateMenge.handle(mengeFehlt);
        assertEquals(400, mengeFehlt.gesetzterStatus);

        // menge = 0
        WarenkorbContextMock mengeNull = new WarenkorbContextMock();
        mengeNull.setPathParam("id", "7");
        mengeNull.setQueryParam("menge", "0");
        controller.updateMenge.handle(mengeNull);
        assertEquals(400, mengeNull.gesetzterStatus);

        // id keine Zahl
        WarenkorbContextMock idKeineZahl = new WarenkorbContextMock();
        idKeineZahl.setPathParam("id", "abc");
        idKeineZahl.setQueryParam("menge", "3");
        controller.updateMenge.handle(idKeineZahl);
        assertEquals(400, idKeineZahl.gesetzterStatus);

        // menge keine Zahl
        WarenkorbContextMock mengeKeineZahl = new WarenkorbContextMock();
        mengeKeineZahl.setPathParam("id", "7");
        mengeKeineZahl.setQueryParam("menge", "abc");
        controller.updateMenge.handle(mengeKeineZahl);
        assertEquals(400, mengeKeineZahl.gesetzterStatus);
    }

    @Test
    void deleteWarenkorbItemMitUngueltigerIdLiefert400() throws Exception {
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("id", "abc");
        WarenkorbController controller = new WarenkorbController(new FakeWarenkorbDao(Collections.emptyList()));

        controller.deleteWarenkorbItem.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
    }

    @Test
    void updateMengeBeiFehlendemItemLiefert404() throws Exception {
        FakeWarenkorbDao dao = new FakeWarenkorbDao(Collections.emptyList());
        dao.updateErgebnis = false;
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("id", "999");
        ctx.setQueryParam("menge", "3");
        WarenkorbController controller = new WarenkorbController(dao);

        controller.updateMenge.handle(ctx);

        assertEquals(404, ctx.gesetzterStatus);
        assertEquals("Warenkorb-Item nicht gefunden.", ctx.gesendetesResult);
    }

    @Test
    void deleteWarenkorbItemBeiFehlendemItemLiefert404() throws Exception {
        FakeWarenkorbDao dao = new FakeWarenkorbDao(Collections.emptyList());
        dao.deleteErgebnis = false;
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("id", "999");
        WarenkorbController controller = new WarenkorbController(dao);

        controller.deleteWarenkorbItem.handle(ctx);

        assertEquals(404, ctx.gesetzterStatus);
        assertEquals("Warenkorb-Item nicht gefunden.", ctx.gesendetesResult);
    }

    @Test
    void warenkorbItemLoeschenUndFehlerfaelleWerdenBehandelt() throws Exception {
        FakeWarenkorbDao dao = new FakeWarenkorbDao(Collections.emptyList());
        WarenkorbContextMock deleteCtx = new WarenkorbContextMock();
        deleteCtx.setPathParam("id", "7");
        WarenkorbController controller = new WarenkorbController(dao);

        controller.deleteWarenkorbItem.handle(deleteCtx);

        assertEquals(200, deleteCtx.gesetzterStatus);
        assertEquals(7, dao.deleteId);

        FakeWarenkorbDao daoOhneEmail = new FakeWarenkorbDao(Collections.emptyList());
        WarenkorbContextMock fehlerCtx = new WarenkorbContextMock();
        fehlerCtx.setQueryParam("artikelId", "5");
        WarenkorbController fehlerController = new WarenkorbController(daoOhneEmail);

        fehlerController.addToWarenkorb.handle(fehlerCtx);

        assertEquals(400, fehlerCtx.gesetzterStatus);
        assertNull(daoOhneEmail.addArtikelId, "DAO darf bei fehlender email nicht aufgerufen werden");

        WarenkorbContextMock dbFehlerCtx = new WarenkorbContextMock();
        dbFehlerCtx.setPathParam("email", "test@example.com");
        WarenkorbController dbFehlerController = new WarenkorbController(new FehlerWarenkorbDao());

        dbFehlerController.getWarenkorb.handle(dbFehlerCtx);

        assertEquals(500, dbFehlerCtx.gesetzterStatus);
        assertEquals("Fehler beim Abrufen des Warenkorbs.", dbFehlerCtx.gesendetesResult,
                "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
    }
}

class FakeWarenkorbDao implements WarenkorbDao {

    private final List<WarenkorbItem> items;

    boolean updateErgebnis = true;
    boolean deleteErgebnis = true;

    String addEmail;
    Integer addArtikelId;
    Integer addMenge;
    Integer updateId;
    Integer updateNeueMenge;
    Integer deleteId;

    FakeWarenkorbDao(List<WarenkorbItem> items) {
        this.items = items;
    }

    @Override
    public List<WarenkorbItem> getWarenkorbByUser(String email) {
        return items;
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) {
        this.addEmail = email;
        this.addArtikelId = artikelId;
        this.addMenge = menge;
    }

    @Override
    public boolean updateMenge(int warenkorbItemId, int menge) {
        this.updateId = warenkorbItemId;
        this.updateNeueMenge = menge;
        return updateErgebnis;
    }

    @Override
    public boolean deleteWarenkorbItem(int warenkorbItemId) {
        this.deleteId = warenkorbItemId;
        return deleteErgebnis;
    }
}

class FehlerWarenkorbDao implements WarenkorbDao {

    @Override
    public List<WarenkorbItem> getWarenkorbByUser(String email) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean updateMenge(int warenkorbItemId, int menge) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }

    @Override
    public boolean deleteWarenkorbItem(int warenkorbItemId) throws DaoException {
        throw new DaoException("Datenbank Fehler");
    }
}


class WarenkorbContextMock extends EinfacherContextMock {

    private final Map<String, String> pathParams = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    String gesendetesResult;

    WarenkorbContextMock() {
        super(null);
    }

    void setPathParam(String key, String value) {
        pathParams.put(key, value);
    }

    void setQueryParam(String key, String value) {
        queryParams.put(key, value);
    }

    @Override
    public String pathParam(String key) {
        return pathParams.getOrDefault(key, "");
    }

    @Override
    public String queryParam(String key) {
        return queryParams.get(key);
    }

    @Override
    public Context result(String result) {
        this.gesendetesResult = result;
        return this;
    }
}
