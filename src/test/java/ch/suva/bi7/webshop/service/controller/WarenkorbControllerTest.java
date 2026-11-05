package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import ch.suva.bi7.webshop.service.mock.ArtikelDaoMock;
import ch.suva.bi7.webshop.service.mock.FakeArtikelDao;
import ch.suva.bi7.webshop.service.mock.WarenkorbDaoMock;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.model.VerfuegbarkeitResponse;
import ch.suva.bi7.webshop.service.model.VerfuegbarkeitsProblemDto;
import ch.suva.bi7.webshop.service.model.WarenkorbEintragDto;
import ch.suva.bi7.webshop.service.service.ArtikelService;
import ch.suva.bi7.webshop.service.service.WarenkorbService;
import ch.suva.bi7.webshop.service.mock.WarenkorbContextMock;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbControllerTest {

    @Test
    void warenkorbAbrufenLiefertItemsAlsJson() throws Exception {
        WarenkorbEintragEntity item = EntityHelper.createWarenkorbEintragEntity(
                1, "test@example.com", 5, 3);
        ArtikelEntity artikel = EntityHelper.createArtikelEntity(
                5, "iPhone 15 Pro", new BigDecimal("1199.00"), null, 3);
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("email", "test@example.com");
        WarenkorbController controller = controller(new WarenkorbDaoMock(List.of(item)), new ArtikelDaoMock(List.of(artikel)));

        controller.ladeWarenkorb.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        List<?> json = (List<?>) ctx.gesendetesJson;
        assertEquals(1, json.size());
        WarenkorbEintragDto eintrag = (WarenkorbEintragDto) json.get(0);
        assertEquals("iPhone 15 Pro", eintrag.getArtikelName());
        assertEquals(new BigDecimal("1199.00"), eintrag.getArtikelPreis());
        assertNull(eintrag.getArtikelBild());
        assertEquals(3, eintrag.getArtikelLagerbestand(), "Der Warenkorb liefert den Lagerbestand des Artikels mit");

        WarenkorbContextMock leererCtx = new WarenkorbContextMock();
        leererCtx.setPathParam("email", "test@example.com");
        WarenkorbController leererController = controller(new WarenkorbDaoMock(Collections.emptyList()), new FakeArtikelDao());

        leererController.ladeWarenkorb.handle(leererCtx);

        assertEquals(200, leererCtx.gesetzterStatus);
        assertTrue(((List<?>) leererCtx.gesendetesJson).isEmpty(), "Ohne Items muss ein leeres JSON kommen");
    }

    @Test
    void artikelHinzufuegenUndMengeAktualisierenFunktionieren() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(List.of(
                EntityHelper.createWarenkorbEintragEntity(7, "test@example.com", 5, 1)));
        WarenkorbContextMock addCtx = new WarenkorbContextMock(Map.of(
                "email", "test@example.com", "artikelId", 5, "menge", 3));
        List<ArtikelEntity> artikelEntityList = new ArrayList<ArtikelEntity>();
        artikelEntityList.add(EntityHelper.createArtikelEntity(5,"Computer",new BigDecimal("1000"),"bild.png"));
        WarenkorbController controller = controller(dao, new ArtikelDaoMock(artikelEntityList));

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
        WarenkorbController controller = controller(new WarenkorbDaoMock(Collections.emptyList()), new FakeArtikelDao());

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
        WarenkorbController controller = controller(new WarenkorbDaoMock(Collections.emptyList()), new FakeArtikelDao());

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
        WarenkorbController controller = controller(new WarenkorbDaoMock(Collections.emptyList()), new FakeArtikelDao());

        controller.loescheWarenkorbEintrag.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
    }

    @Test
    void updateMengeBeiFehlendemItemLiefert404() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        dao.updateErgebnis = false;
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("menge", 3));
        ctx.setPathParam("id", "999");
        WarenkorbController controller = controller(dao, new FakeArtikelDao());

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
        WarenkorbController controller = controller(dao, new FakeArtikelDao());

        controller.loescheWarenkorbEintrag.handle(ctx);

        assertEquals(404, ctx.gesetzterStatus);
        assertEquals("Warenkorb-Item nicht gefunden.", ctx.gesendetesResult);
    }

    @Test
    void warenkorbEintragLoeschenUndFehlerfaelleWerdenBehandelt() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock deleteCtx = new WarenkorbContextMock();
        deleteCtx.setPathParam("id", "7");
        WarenkorbController controller = controller(dao, new FakeArtikelDao());

        controller.loescheWarenkorbEintrag.handle(deleteCtx);

        assertEquals(200, deleteCtx.gesetzterStatus);
        assertEquals(7, dao.deleteId);

        WarenkorbDaoMock daoOhneEmail = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock fehlerCtx = new WarenkorbContextMock(
                Map.of("artikelId", 5));
        WarenkorbController fehlerController = controller(daoOhneEmail, new FakeArtikelDao());

        fehlerController.fuegeArtikelZuWarenkorbHinzu.handle(fehlerCtx);

        assertEquals(400, fehlerCtx.gesetzterStatus);
        assertNull(daoOhneEmail.addArtikelId, "DAO darf bei fehlender email nicht aufgerufen werden");

        WarenkorbContextMock dbFehlerCtx = new WarenkorbContextMock();
        dbFehlerCtx.setPathParam("email", "test@example.com");
        WarenkorbController dbFehlerController = controller(new WarenkorbDaoMock(true), new FakeArtikelDao());

        dbFehlerController.ladeWarenkorb.handle(dbFehlerCtx);

        assertEquals(500, dbFehlerCtx.gesetzterStatus);
        assertEquals("Fehler beim Abrufen des Warenkorbs.", dbFehlerCtx.gesendetesResult,
                "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
    }

    @Test
    void verfuegbarkeitOkWennMengeKleinerOderGleichLagerbestand() throws Exception {
        WarenkorbContextMock ctx = verfuegbarkeitPruefen(
                List.of(EntityHelper.createWarenkorbEintragEntity(1, "test@example.com", 5, 2),
                        EntityHelper.createWarenkorbEintragEntity(2, "test@example.com", 6, 5)),
                List.of(EntityHelper.createArtikelEntity(5, "iPhone 15 Pro", new BigDecimal("1199.00"), null, 6),
                        EntityHelper.createArtikelEntity(6, "Samsung Galaxy S24", new BigDecimal("899.90"), null, 5)));

        assertEquals(200, ctx.gesetzterStatus);
        VerfuegbarkeitResponse antwort = (VerfuegbarkeitResponse) ctx.gesendetesJson;
        assertTrue(antwort.allesVerfuegbar, "Grenzfall: Menge 5 bei Lagerbestand 5 ist noch verfügbar");
        assertTrue(antwort.probleme.isEmpty());
    }

    @Test
    void verfuegbarkeitMeldetZuGrosseMenge() throws Exception {
        WarenkorbContextMock ctx = verfuegbarkeitPruefen(
                List.of(EntityHelper.createWarenkorbEintragEntity(1, "test@example.com", 5, 3)),
                List.of(EntityHelper.createArtikelEntity(5, "iPhone 15 Pro", new BigDecimal("1199.00"), null, 2)));

        assertEquals(200, ctx.gesetzterStatus);
        VerfuegbarkeitResponse antwort = (VerfuegbarkeitResponse) ctx.gesendetesJson;
        assertFalse(antwort.allesVerfuegbar);
        assertEquals(1, antwort.probleme.size());
        VerfuegbarkeitsProblemDto problem = antwort.probleme.get(0);
        assertEquals(1, problem.warenkorbItemId);
        assertEquals(5, problem.artikelId);
    }

    @Test
    void verfuegbarkeitMeldetAusverkauftenArtikel() throws Exception {
        WarenkorbContextMock ctx = verfuegbarkeitPruefen(
                List.of(EntityHelper.createWarenkorbEintragEntity(1, "test@example.com", 5, 1)),
                List.of(EntityHelper.createArtikelEntity(5, "iPhone 15 Pro", new BigDecimal("1199.00"), null, 0)));

        VerfuegbarkeitResponse antwort = (VerfuegbarkeitResponse) ctx.gesendetesJson;
        assertFalse(antwort.allesVerfuegbar);
        assertEquals(1, antwort.probleme.get(0).warenkorbItemId);
    }

    @Test
    void verfuegbarkeitMeldetNurDenBetroffenenArtikel() throws Exception {
        WarenkorbContextMock ctx = verfuegbarkeitPruefen(
                List.of(EntityHelper.createWarenkorbEintragEntity(1, "test@example.com", 5, 1),
                        EntityHelper.createWarenkorbEintragEntity(2, "test@example.com", 6, 4)),
                List.of(EntityHelper.createArtikelEntity(5, "iPhone 15 Pro", new BigDecimal("1199.00"), null, 10),
                        EntityHelper.createArtikelEntity(6, "Samsung Galaxy S24", new BigDecimal("899.90"), null, 3)));

        VerfuegbarkeitResponse antwort = (VerfuegbarkeitResponse) ctx.gesendetesJson;
        assertFalse(antwort.allesVerfuegbar, "Ein einziger fehlender Artikel reicht, um den Checkout zu stoppen");
        assertEquals(1, antwort.probleme.size());
        assertEquals(2, antwort.probleme.get(0).warenkorbItemId, "Die Meldung muss beim richtigen Eintrag landen");
    }

    @Test
    void verfuegbarkeitMeldetGeloeschtenArtikel() throws Exception {
        WarenkorbContextMock ctx = verfuegbarkeitPruefen(
                List.of(EntityHelper.createWarenkorbEintragEntity(1, "test@example.com", 99, 1)),
                new ArrayList<>());

        VerfuegbarkeitResponse antwort = (VerfuegbarkeitResponse) ctx.gesendetesJson;
        assertFalse(antwort.allesVerfuegbar);
        assertEquals(99, antwort.probleme.get(0).artikelId);
    }

    @Test
    void verfuegbarkeitBeiDbFehlerLiefert500() throws Exception {
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("email", "test@example.com");

        controller(new WarenkorbDaoMock(true), new FakeArtikelDao()).pruefeVerfuegbarkeit.handle(ctx);

        assertEquals(500, ctx.gesetzterStatus);
        assertEquals("Fehler bei der Verfügbarkeitsprüfung.", ctx.gesendetesResult);
    }

    // ---------- Lagerbestand schon beim Hinzufügen / Ändern prüfen ----------

    @Test
    void hinzufuegenMitMengeUeberLagerbestandLiefert409() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("email", "test@example.com", "artikelId", 5, "menge", 4));

        controller(dao, artikelMitBestand(3)).fuegeArtikelZuWarenkorbHinzu.handle(ctx);

        assertEquals(409, ctx.gesetzterStatus);
        assertEquals("Von \"Samsung Galaxy S24\" sind nur noch 3 Stück verfügbar.",
                ((Map<?, ?>) ctx.gesendetesJson).get("error"));
        assertNull(dao.addArtikelId, "Bei zu wenig Lagerbestand darf nichts in den Warenkorb kommen");
    }

    @Test
    void hinzufuegenZaehltBereitsVorhandeneMengeMit() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(List.of(
                EntityHelper.createWarenkorbEintragEntity(1, "test@example.com", 5, 3)));
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("email", "test@example.com", "artikelId", 5, "menge", 3));

        controller(dao, artikelMitBestand(5)).fuegeArtikelZuWarenkorbHinzu.handle(ctx);

        assertEquals(409, ctx.gesetzterStatus, "3 im Warenkorb + 3 neu = 6 > Bestand 5");
        assertTrue(((String) ((Map<?, ?>) ctx.gesendetesJson).get("error")).contains("Davon hast du bereits 3 im Warenkorb."));
        assertNull(dao.addArtikelId);
    }

    @Test
    void hinzufuegenBisGenauZumLagerbestandIstErlaubt() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(List.of(
                EntityHelper.createWarenkorbEintragEntity(1, "test@example.com", 5, 2)));
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("email", "test@example.com", "artikelId", 5, "menge", 3));

        controller(dao, artikelMitBestand(5)).fuegeArtikelZuWarenkorbHinzu.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Grenzfall: 2 + 3 = 5 bei Bestand 5");
        assertEquals(3, dao.addMenge);
    }

    @Test
    void hinzufuegenEinesAusverkauftenArtikelsLiefert409() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("email", "test@example.com", "artikelId", 5, "menge", 1));

        controller(dao, artikelMitBestand(0)).fuegeArtikelZuWarenkorbHinzu.handle(ctx);

        assertEquals(409, ctx.gesetzterStatus);
        assertEquals("\"Samsung Galaxy S24\" ist ausverkauft.", ((Map<?, ?>) ctx.gesendetesJson).get("error"));
    }

    @Test
    void hinzufuegenEinesUnbekanntenArtikelsLiefert400() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(Collections.emptyList());
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("email", "test@example.com", "artikelId", 99, "menge", 1));

        controller(dao, new FakeArtikelDao()).fuegeArtikelZuWarenkorbHinzu.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
        assertNull(dao.addArtikelId);
    }

    @Test
    void mengeErhoehenUeberLagerbestandLiefert409() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(List.of(
                EntityHelper.createWarenkorbEintragEntity(7, "test@example.com", 5, 3)));
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("menge", 4));
        ctx.setPathParam("id", "7");

        controller(dao, artikelMitBestand(3)).aktualisiereMenge.handle(ctx);

        assertEquals(409, ctx.gesetzterStatus);
        assertNull(dao.updateId, "Die Menge darf nicht über den Lagerbestand erhöht werden");
    }

    @Test
    void mengeErhoehenBisGenauZumLagerbestandIstErlaubt() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(List.of(
                EntityHelper.createWarenkorbEintragEntity(7, "test@example.com", 5, 2)));
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("menge", 3));
        ctx.setPathParam("id", "7");

        controller(dao, artikelMitBestand(3)).aktualisiereMenge.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        assertEquals(3, dao.updateNeueMenge);
    }

    @Test
    void mengeReduzierenIstImmerErlaubtAuchUeberLagerbestand() throws Exception {
        WarenkorbDaoMock dao = new WarenkorbDaoMock(List.of(
                EntityHelper.createWarenkorbEintragEntity(7, "test@example.com", 5, 5)));
        WarenkorbContextMock ctx = new WarenkorbContextMock(Map.of("menge", 4));
        ctx.setPathParam("id", "7");

        controller(dao, artikelMitBestand(3)).aktualisiereMenge.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus, "Von 5 auf 4 reduzieren muss gehen, auch wenn nur 3 an Lager sind");
        assertEquals(4, dao.updateNeueMenge);
    }

    private static ArtikelDaoMock artikelMitBestand(int lagerbestand) {
        return new ArtikelDaoMock(List.of(EntityHelper.createArtikelEntity(
                5, "Samsung Galaxy S24", new BigDecimal("899.90"), null, lagerbestand)));
    }

        private static WarenkorbContextMock verfuegbarkeitPruefen(List<WarenkorbEintragEntity> warenkorb,
                                                              List<ArtikelEntity> artikel) throws Exception {
        WarenkorbContextMock ctx = new WarenkorbContextMock();
        ctx.setPathParam("email", "test@example.com");
        controller(new WarenkorbDaoMock(warenkorb), new ArtikelDaoMock(artikel)).pruefeVerfuegbarkeit.handle(ctx);
        return ctx;
    }

    private static WarenkorbController controller(WarenkorbDao dao, ArtikelDao artikelDao) {
        return new WarenkorbController(new WarenkorbService(dao, artikelDao), new ArtikelService(artikelDao));
    }
}
