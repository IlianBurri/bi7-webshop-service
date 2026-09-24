package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import ch.suva.bi7.webshop.service.mock.WarenkorbDaoMock;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.service.WarenkorbService;
import ch.suva.bi7.webshop.service.mock.WarenkorbContextMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbControllerTest {

    @Test
    void warenkorbAbrufenLiefertItemsAlsJson() throws Exception {
        WarenkorbEintragEntity item = EntityHelper.createWarenkorbEintragEntity(
                1, "test@example.com", 5, 3,
                "iPhone 15 Pro", new BigDecimal("1199.00"), null);
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("email", "test@example.com");
        WarenkorbController controller = controller(new WarenkorbDaoMock(List.of(item)));

        controller.ladeWarenkorb.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        List<?> json = (List<?>) ctx.gesendetesJson;
        assertEquals(1, json.size());

        WarenkorbContextMock leererCtx = new WarenkorbContextMock();
        leererCtx.setPathParam("email", "test@example.com");
        WarenkorbController leererController = controller(new WarenkorbDaoMock(Collections.emptyList()));

        leererController.ladeWarenkorb.handle(leererCtx);

        assertEquals(200, leererCtx.gesetzterStatus);
        assertTrue(((List<?>) leererCtx.gesendetesJson).isEmpty(), "Ohne Items muss ein leeres JSON kommen");
    }

    @Test
    void artikelHinzufuegenUndMengeAktualisierenFunktionieren() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock addCtx = new WarenkorbContextMock(Map.of(
                "email", "test@example.com", "artikelId", 5, "menge", 3));
        WarenkorbController controller = controller(dao);

        controller.fuegeArtikelZuWarenkorbHinzu.handle(addCtx);

        assertEquals(201, addCtx.gesetzterStatus);
        assertEquals("test@example.com", dao.addEmail);
        assertEquals(5, dao.addArtikelId);
        assertEquals(3, dao.addMenge);

        WarenkorbContextMock updateCtx = new WarenkorbContextMock(Map.of("menge", 5));
        updateCtx.setPathParam("id", "7");

        controller.aktualisiereMenge.handle(updateCtx);

        assertEquals(200, updateCtx.gesetzterStatus);
        assertEquals(7, dao.updateId);
        assertEquals(5, dao.updateNeueMenge);
    }

    @Test
    void addToWarenkorbMitUngueltigenParameternLiefert400() throws Exception {
        WarenkorbController controller = controller(new WarenkorbDaoMock(Collections.emptyList()));

        WarenkorbContextMock fehlendeArtikelId = new WarenkorbContextMock(
                Map.of("email", "test@example.com"));
        controller.fuegeArtikelZuWarenkorbHinzu.handle(fehlendeArtikelId);
        assertEquals(400, fehlendeArtikelId.gesetzterStatus);

        WarenkorbContextMock mengeNull = new WarenkorbContextMock(Map.of(
                "email", "test@example.com", "artikelId", 5, "menge", 0));
        controller.fuegeArtikelZuWarenkorbHinzu.handle(mengeNull);
        assertEquals(400, mengeNull.gesetzterStatus);

        WarenkorbContextMock negativeMenge = new WarenkorbContextMock(Map.of(
                "email", "test@example.com", "artikelId", 5, "menge", -2));
        controller.fuegeArtikelZuWarenkorbHinzu.handle(negativeMenge);
        assertEquals(400, negativeMenge.gesetzterStatus);

        WarenkorbContextMock artikelIdKeineZahl = new WarenkorbContextMock(Map.of(
                "email", "test@example.com", "artikelId", "abc"));
        controller.fuegeArtikelZuWarenkorbHinzu.handle(artikelIdKeineZahl);
        assertEquals(400, artikelIdKeineZahl.gesetzterStatus);

        WarenkorbContextMock mengeKeineZahl = new WarenkorbContextMock(Map.of(
                "email", "test@example.com", "artikelId", 5, "menge", "abc"));
        controller.fuegeArtikelZuWarenkorbHinzu.handle(mengeKeineZahl);
        assertEquals(400, mengeKeineZahl.gesetzterStatus);
    }

    @Test
    void updateMengeMitUngueltigenParameternLiefert400() throws Exception {
        WarenkorbController controller = controller(new WarenkorbDaoMock(Collections.emptyList()));

        WarenkorbContextMock mengeFehlt = new WarenkorbContextMock(Map.of());
        mengeFehlt.setPathParam("id", "7");
        controller.aktualisiereMenge.handle(mengeFehlt);
        assertEquals(400, mengeFehlt.gesetzterStatus);

        WarenkorbContextMock mengeNull = new WarenkorbContextMock(Map.of("menge", 0));
        mengeNull.setPathParam("id", "7");
        controller.aktualisiereMenge.handle(mengeNull);
        assertEquals(400, mengeNull.gesetzterStatus);

        WarenkorbContextMock idKeineZahl = new WarenkorbContextMock(Map.of("menge", 3));
        idKeineZahl.setPathParam("id", "abc");
        controller.aktualisiereMenge.handle(idKeineZahl);
        assertEquals(400, idKeineZahl.gesetzterStatus);

        WarenkorbContextMock mengeKeineZahl = new WarenkorbContextMock(Map.of("menge", "abc"));
        mengeKeineZahl.setPathParam("id", "7");
        controller.aktualisiereMenge.handle(mengeKeineZahl);
        assertEquals(400, mengeKeineZahl.gesetzterStatus);
    }

    @Test
    void deleteWarenkorbEintragMitUngueltigerIdLiefert400() throws Exception {
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("id", "abc");
        WarenkorbController controller = controller(new WarenkorbDaoMock(Collections.emptyList()));

        controller.loescheWarenkorbEintrag.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
    }

    @Test
    void updateMengeBeiFehlendemItemLiefert404() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        dao.updateErgebnis = false;
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("menge", 3));
        ctx.setPathParam("id", "999");
        WarenkorbController controller = controller(dao);

        controller.aktualisiereMenge.handle(ctx);

        assertEquals(404, ctx.gesetzterStatus);
        assertEquals("Warenkorb-Item nicht gefunden.", ctx.gesendetesResult);
    }

    @Test
    void deleteWarenkorbEintragBeiFehlenderPositionLiefert404() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        dao.deleteErgebnis = false;
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("id", "999");
        WarenkorbController controller = controller(dao);

        controller.loescheWarenkorbEintrag.handle(ctx);

        assertEquals(404, ctx.gesetzterStatus);
        assertEquals("Warenkorb-Item nicht gefunden.", ctx.gesendetesResult);
    }

    @Test
    void warenkorbEintragLoeschenUndFehlerfaelleWerdenBehandelt() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock deleteCtx = new WarenkorbContextMock();
        deleteCtx.setPathParam("id", "7");
        WarenkorbController controller = controller(dao);

        controller.loescheWarenkorbEintrag.handle(deleteCtx);

        assertEquals(200, deleteCtx.gesetzterStatus);
        assertEquals(7, dao.deleteId);

        WarenkorbDaoMock daoOhneEmail = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock fehlerCtx = new WarenkorbContextMock(
                Map.of("artikelId", 5));
        WarenkorbController fehlerController = controller(daoOhneEmail);

        fehlerController.fuegeArtikelZuWarenkorbHinzu.handle(fehlerCtx);

        assertEquals(400, fehlerCtx.gesetzterStatus);
        assertNull(daoOhneEmail.addArtikelId, "DAO darf bei fehlender email nicht aufgerufen werden");

        WarenkorbContextMock dbFehlerCtx = new WarenkorbContextMock();
        dbFehlerCtx.setPathParam("email", "test@example.com");
        WarenkorbController dbFehlerController = controller(new WarenkorbDaoMock(true));

        dbFehlerController.ladeWarenkorb.handle(dbFehlerCtx);

        assertEquals(500, dbFehlerCtx.gesetzterStatus);
        assertEquals("Fehler beim Abrufen des Warenkorbs.", dbFehlerCtx.gesendetesResult,
                "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
    }
    private static WarenkorbController controller(WarenkorbDao dao) {
        return new WarenkorbController(new WarenkorbService(dao));
    }
}
