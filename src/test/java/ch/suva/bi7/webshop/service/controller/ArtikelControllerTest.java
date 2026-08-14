package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Artikel;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelControllerTest {

    static class FakeArtikelDao implements ArtikelDao {

        private final List<Artikel> artikel;
        int callCount = 0;

        FakeArtikelDao(List<Artikel> artikel) {
            this.artikel = artikel;
        }

        @Override
        public List<Artikel> getAllArtikel() {
            callCount++;
            return artikel;
        }
    }


    static class FehlerArtikelDao implements ArtikelDao {

        int callCount = 0;

        @Override
        public List<Artikel> getAllArtikel() throws Exception {
            callCount++;
            throw new Exception("Datenbank Fehler");
        }
    }


    // Statischen DAO-Zustand pro Test zurücksetzen, damit keine Test-Interferenzen entstehen
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
}


class ArtikelContextMock extends EinfacherContextMock {

    String gesendetesResult;

    ArtikelContextMock() {
        super(null);
    }

    @Override
    public Context result(String result) {
        this.gesendetesResult = result;
        return this;
    }
}
