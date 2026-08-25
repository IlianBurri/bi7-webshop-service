package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.AddArtikelRequest;
import ch.suva.bi7.webshop.service.model.Artikel;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelControllerTest {

    static class FakeArtikelDao implements ArtikelDao {

        private final List<Artikel> artikel;
        int callCount = 0;
        int addArtikelCallCount = 0;
        int generierterKey = 1;

        FakeArtikelDao(List<Artikel> artikel) {
            this.artikel = artikel;
        }

        @Override
        public List<Artikel> getAllArtikel() {
            callCount++;
            return artikel;
        }

        @Override
        public int addArtikel(String name, BigDecimal preis, String bild) {
            addArtikelCallCount++;
            return generierterKey;
        }
    }


    static class FehlerArtikelDao implements ArtikelDao {

        int callCount = 0;

        @Override
        public List<Artikel> getAllArtikel() throws Exception {
            callCount++;
            throw new Exception("Datenbank Fehler");
        }

        @Override
        public int addArtikel(String name, BigDecimal preis, String bild) throws Exception {
            callCount++;
            throw new Exception("Datenbank Fehler");
        }
    }


    @BeforeEach
    void resetArtikelDaoMock() {
        ArtikelController.setArtikelDaoMock(new FakeArtikelDao(Collections.emptyList()));
    }


    @Test
    void fetchAllArtikelLiefertArtikellisteAlsJson() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(List.of(
                new Artikel(1, "iPhone 15 Pro", new BigDecimal("1199.00"), "https://example.com/iphone.jpg"),
                new Artikel(2, "Samsung Galaxy S24", new BigDecimal("899.90"), "https://example.com/galaxy.jpg")
        ));
        ArtikelController.setArtikelDaoMock(dao);

        ArtikelContextMock ctx = new ArtikelContextMock();
        ArtikelController.fetchAllArtikel.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        List<?> json = (List<?>) ctx.gesendetesJson;
        assertEquals(2, json.size(), "Es müssen 2 Artikel im JSON stehen");
        assertEquals(1, dao.callCount, "Das DAO muss genau einmal aufgerufen werden");

        Artikel erster = (Artikel) json.get(0);
        assertEquals("iPhone 15 Pro", erster.name);
        assertEquals(new BigDecimal("1199.00"), erster.preis);
        assertEquals("https://example.com/iphone.jpg", erster.bild);

        Artikel zweiter = (Artikel) json.get(1);
        assertEquals("Samsung Galaxy S24", zweiter.name);
        assertEquals(new BigDecimal("899.90"), zweiter.preis);
        assertEquals("https://example.com/galaxy.jpg", zweiter.bild);
    }


    @Test
    void fetchAllArtikelBeiLeererListeLiefertLeeresJson() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        ArtikelContextMock ctx = new ArtikelContextMock();
        ArtikelController.fetchAllArtikel.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        assertNotNull(ctx.gesendetesJson);
        assertTrue(((List<?>) ctx.gesendetesJson).isEmpty(), "Ohne Artikel muss ein leeres JSON kommen");
        assertEquals(1, dao.callCount, "Das DAO muss genau einmal aufgerufen werden");
    }


    @Test
    void fetchAllArtikelBeiExceptionLiefertKonkreteFehlermeldung() throws Exception {
        FehlerArtikelDao dao = new FehlerArtikelDao();
        ArtikelController.setArtikelDaoMock(dao);

        ArtikelContextMock ctx = new ArtikelContextMock();
        ArtikelController.fetchAllArtikel.handle(ctx);

        assertEquals(500, ctx.gesetzterStatus);
        assertEquals("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.",
                ctx.gesendetesResult, "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
        assertEquals(1, dao.callCount, "Das DAO muss genau einmal aufgerufen werden");
    }


    @Test
    void addArtikelLiefert201MitNeuemArtikel() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        dao.generierterKey = 42;
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), "https://example.com/iphone16.jpg");
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Bei Erfolg muss Status 201 kommen");
        assertEquals(1, dao.addArtikelCallCount, "Das DAO muss genau einmal aufgerufen werden");

        Artikel artikel = (Artikel) ctx.gesendetesJson;
        assertEquals(42, artikel.artikelId, "Die generierte artikelId muss zurückgegeben werden");
        assertEquals("iPhone 16 Pro", artikel.name);
        assertEquals(new BigDecimal("1299.00"), artikel.preis);
        assertEquals("https://example.com/iphone16.jpg", artikel.bild);
    }

    @Test
    void addArtikelMitZuLangemNamenLiefert400() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "a".repeat(256), new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Name > 255 Zeichen muss abgelehnt werden");
        assertEquals(0, dao.addArtikelCallCount, "Bei ungültiger Eingabe darf das DAO nicht aufgerufen werden");
        assertTrue(((Map<?, ?>) ctx.gesendetesJson).containsKey("error"));
    }

    @Test
    void addArtikelMitPreisUnterMindestpreisLiefert400() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("0.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Preis unter 0.01 muss abgelehnt werden");
        assertEquals(0, dao.addArtikelCallCount);
    }

    @Test
    void addArtikelMitPreisUeberMaximalpreisLiefert400() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("100000000.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Preis über 99999999.99 muss abgelehnt werden");
        assertEquals(0, dao.addArtikelCallCount);
    }

    @Test
    void addArtikelMitZuLangemBildLiefert400() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("10.00"), "b".repeat(501));
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Bild > 500 Zeichen muss abgelehnt werden");
        assertEquals(0, dao.addArtikelCallCount);
    }

    @Test
    void addArtikelMitLeeremNamenLiefert400() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "   ", new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Leerer Name muss abgelehnt werden");
        assertEquals(0, dao.addArtikelCallCount);
    }

    @Test
    void addArtikelOhneAdminSessionLiefert403() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(403, ctx.gesetzterStatus, "Ohne Admin-Session muss 403 kommen");
        assertEquals(0, dao.addArtikelCallCount, "Ohne Admin darf das DAO nicht aufgerufen werden");
        assertEquals("Nur Administratoren dürfen Artikel anlegen.",
                ((Map<?, ?>) ctx.gesendetesJson).get("error"));
    }

    @Test
    void addArtikelMitNichtAdminSessionLiefert403() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", false);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(403, ctx.gesetzterStatus, "Mit isAdmin=false muss 403 kommen");
        assertEquals(0, dao.addArtikelCallCount, "Nicht-Admins dürfen das DAO nicht aufrufen");
    }

    @Test
    void addArtikelBeiExceptionLiefert500() throws Exception {
        FehlerArtikelDao dao = new FehlerArtikelDao();
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(500, ctx.gesetzterStatus);
        assertEquals("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.",
                ctx.gesendetesResult, "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
        assertEquals(1, dao.callCount, "Das DAO muss genau einmal aufgerufen werden");
    }

    @Test
    void fetchAllArtikelRuftDaoGenauEinmalAuf() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(List.of(
                new Artikel(1, "iPhone 15 Pro", new BigDecimal("1199.00"), "https://example.com/iphone.jpg")
        ));
        ArtikelController.setArtikelDaoMock(dao);

        ArtikelContextMock ctx = new ArtikelContextMock();
        ArtikelController.fetchAllArtikel.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        assertEquals(1, dao.callCount, "fetchAllArtikel muss das DAO genau einmal verwenden");
    }

    @Test
    void addArtikelMitPreisGenauAmMindestpreisWirdAkzeptiert() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "Günstigster Artikel", new BigDecimal("0.01"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Preis genau am Mindestpreis (0.01) muss akzeptiert werden");
        assertEquals(1, dao.addArtikelCallCount);
    }

    @Test
    void addArtikelMitPreisGenauAmMaximalpreisWirdAkzeptiert() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "Teuerster Artikel", new BigDecimal("99999999.99"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Preis genau am Maximalpreis muss akzeptiert werden");
        assertEquals(1, dao.addArtikelCallCount);
    }

    @Test
    void addArtikelMitNameGenauAnMaxLaengeWirdAkzeptiert() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "a".repeat(255), new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Name mit genau 255 Zeichen muss noch akzeptiert werden");
    }

    @Test
    void addArtikelMitBildGenauAnMaxLaengeWirdAkzeptiert() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "Name", new BigDecimal("10.00"), "b".repeat(500));
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Bild mit genau 500 Zeichen muss noch akzeptiert werden");
    }

    @Test
    void addArtikelOhneBildWirdAkzeptiert() throws Exception {
        FakeArtikelDao dao = new FakeArtikelDao(Collections.emptyList());
        ArtikelController.setArtikelDaoMock(dao);

        AddArtikelRequest request = new AddArtikelRequest(
                "Name", new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("isAdmin", true);

        ArtikelController.addArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Bild ist laut validiere() optional (nur Länge wird geprüft, falls vorhanden)");
        assertEquals(1, dao.addArtikelCallCount);
    }
}


class ArtikelContextMock extends EinfacherContextMock {

    String gesendetesResult;

    ArtikelContextMock() {
        this(null);
    }

    ArtikelContextMock(Object body) {
        super(body);
    }

    @Override
    public Context result(String result) {
        this.gesendetesResult = result;
        return this;
    }
}
