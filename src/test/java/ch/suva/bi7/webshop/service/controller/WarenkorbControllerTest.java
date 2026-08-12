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
        WarenkorbController.setWarenkorbDaoMock(new FakeWarenkorbDao(List.of(item)));

        WarenkorbController.getWarenkorb.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        List<?> json = (List<?>) ctx.gesendetesJson;
        assertEquals(1, json.size());

        WarenkorbContextMock leererCtx = new WarenkorbContextMock();
        leererCtx.setPathParam("email", "test@example.com");
        WarenkorbController.setWarenkorbDaoMock(new FakeWarenkorbDao(Collections.emptyList()));

        WarenkorbController.getWarenkorb.handle(leererCtx);

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
        WarenkorbController.setWarenkorbDaoMock(dao);

        WarenkorbController.addToWarenkorb.handle(addCtx);

        assertEquals(201, addCtx.gesetzterStatus);
        assertEquals("test@example.com", dao.addEmail);
        assertEquals(5, dao.addArtikelId);
        assertEquals(3, dao.addMenge);

        WarenkorbContextMock updateCtx = new WarenkorbContextMock();
        updateCtx.setPathParam("id", "7");
        updateCtx.setQueryParam("menge", "5");
        WarenkorbController.setWarenkorbDaoMock(dao);

        WarenkorbController.updateMenge.handle(updateCtx);

        assertEquals(200, updateCtx.gesetzterStatus);
        assertEquals(7, dao.updateId);
        assertEquals(5, dao.updateNeueMenge);
    }

    @Test
    void warenkorbItemLoeschenUndFehlerfaelleWerdenBehandelt() throws Exception {
        FakeWarenkorbDao dao = new FakeWarenkorbDao(Collections.emptyList());
        WarenkorbContextMock deleteCtx = new WarenkorbContextMock();
        deleteCtx.setPathParam("id", "7");
        WarenkorbController.setWarenkorbDaoMock(dao);

        WarenkorbController.deleteWarenkorbItem.handle(deleteCtx);

        assertEquals(200, deleteCtx.gesetzterStatus);
        assertEquals(7, dao.deleteId);

        FakeWarenkorbDao daoOhneEmail = new FakeWarenkorbDao(Collections.emptyList());
        WarenkorbContextMock fehlerCtx = new WarenkorbContextMock();
        fehlerCtx.setQueryParam("artikelId", "5");
        WarenkorbController.setWarenkorbDaoMock(daoOhneEmail);

        WarenkorbController.addToWarenkorb.handle(fehlerCtx);

        assertEquals(400, fehlerCtx.gesetzterStatus);
        assertNull(daoOhneEmail.addArtikelId, "DAO darf bei fehlender email nicht aufgerufen werden");

        WarenkorbContextMock dbFehlerCtx = new WarenkorbContextMock();
        dbFehlerCtx.setPathParam("email", "test@example.com");
        WarenkorbController.setWarenkorbDaoMock(new FehlerWarenkorbDao());

        WarenkorbController.getWarenkorb.handle(dbFehlerCtx);

        assertEquals(500, dbFehlerCtx.gesetzterStatus);
        assertNotNull(dbFehlerCtx.gesendetesResult);
    }
}

class FakeWarenkorbDao implements WarenkorbDao {

    private final List<WarenkorbItem> items;

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
    public void updateMenge(int warenkorbItemId, int menge) {
        this.updateId = warenkorbItemId;
        this.updateNeueMenge = menge;
    }

    @Override
    public void deleteWarenkorbItem(int warenkorbItemId) {
        this.deleteId = warenkorbItemId;
    }
}

class FehlerWarenkorbDao implements WarenkorbDao {

    @Override
    public List<WarenkorbItem> getWarenkorbByUser(String email) throws Exception {
        throw new Exception("Datenbank Fehler");
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) throws Exception {
        throw new Exception("Datenbank Fehler");
    }

    @Override
    public void updateMenge(int warenkorbItemId, int menge) throws Exception {
        throw new Exception("Datenbank Fehler");
    }

    @Override
    public void deleteWarenkorbItem(int warenkorbItemId) throws Exception {
        throw new Exception("Datenbank Fehler");
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
