package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BenutzerDao;
import ch.suva.bi7.webshop.service.mock.*;
import ch.suva.bi7.webshop.service.model.AddArtikelRequest;
import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import ch.suva.bi7.webshop.service.model.AddArtikelResponse;
import ch.suva.bi7.webshop.service.model.ArtikelDto;
import ch.suva.bi7.webshop.service.service.ArtikelService;
import ch.suva.bi7.webshop.service.service.FakeArtikelService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelControllerTest {

    @Test
    void fetchAllArtikelLiefertArtikellisteAlsJson() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(List.of(
                new ArtikelDto(1, "iPhone 15 Pro", new BigDecimal("1199.00"), "https://example.com/iphone.jpg"),
                new ArtikelDto(2, "Samsung Galaxy S24", new BigDecimal("899.90"), "https://example.com/galaxy.jpg")
        ));
        ArtikelController testee = createTestee(fakeArtikelService, null);

        ArtikelContextMock ctx = new ArtikelContextMock();
        testee.ladeAlleArtikel.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        List<?> json = (List<?>) ctx.gesendetesJson;
        assertEquals(2, json.size(), "Es müssen 2 Artikel im JSON stehen");
        assertEquals(1, fakeArtikelService.callCount, "Das DAO muss genau einmal aufgerufen werden");

        ArtikelDto erster = (ArtikelDto) json.get(0);
        assertEquals("iPhone 15 Pro", erster.getName());
        assertEquals(new BigDecimal("1199.00"), erster.getPreis());
        assertEquals("https://example.com/iphone.jpg", erster.getBild());

        ArtikelDto zweiter = (ArtikelDto) json.get(1);
        assertEquals("Samsung Galaxy S24", zweiter.getName());
        assertEquals(new BigDecimal("899.90"), zweiter.getPreis());
        assertEquals("https://example.com/galaxy.jpg", zweiter.getBild());
    }


    @Test
    void fetchAllArtikelBeiLeererListeLiefertLeeresJson() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        ArtikelController testee = createTestee(fakeArtikelService, null);

        ArtikelContextMock ctx = new ArtikelContextMock();
        testee.ladeAlleArtikel.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        assertNotNull(ctx.gesendetesJson);
        assertTrue(((List<?>) ctx.gesendetesJson).isEmpty(), "Ohne Artikel muss ein leeres JSON kommen");
        assertEquals(1, fakeArtikelService.callCount, "Das DAO muss genau einmal aufgerufen werden");
    }


    @Test
    void fetchAllArtikelBeiExceptionLiefertKonkreteFehlermeldung() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList(), true);
        ArtikelController testee = createTestee(fakeArtikelService, null);

        ArtikelContextMock ctx = new ArtikelContextMock();
        testee.ladeAlleArtikel.handle(ctx);

        assertEquals(500, ctx.gesetzterStatus);
        assertEquals("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.",
                ctx.gesendetesResult, "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
        assertEquals(1, fakeArtikelService.callCount, "Das DAO muss genau einmal aufgerufen werden");
    }


    @Test
    void addArtikelLiefert201MitNeuemArtikel() throws Exception {
        FakeArtikelService fakeArtikelService = getFakeArtikelServiceOneArtikel();
        fakeArtikelService.generierterKey = 42;
        BenutzerDao benutzerDao = new EinfachesBenutzerDaoMock(Optional.of(
                new BenutzerEntity("Admin", "admin@example.com", "password", true)));
        ArtikelController testee = createTestee(fakeArtikelService, benutzerDao);
        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), "https://example.com/iphone16.jpg");
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Bei Erfolg muss Status 201 kommen");
        assertEquals(1, fakeArtikelService.addArtikelCallCount, "Das DAO muss genau einmal aufgerufen werden");

        AddArtikelResponse response = (AddArtikelResponse) ctx.gesendetesJson;
        ArtikelDto artikel = response.getArtikel();
        assertEquals(42, artikel.getArtikelId(), "Die generierte artikelId muss zurückgegeben werden");
        assertEquals("iPhone 15 Pro", artikel.getName());
        assertEquals(new BigDecimal("1199.00"), artikel.getPreis());
        assertEquals("https://example.com/iphone.jpg", artikel.getBild());
    }

    @Test
    void addArtikelMitZuLangemNamenLiefert400() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "a".repeat(256), new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Name > 255 Zeichen muss abgelehnt werden");
        assertEquals(0, fakeArtikelService.addArtikelCallCount, "Bei ungültiger Eingabe darf das DAO nicht aufgerufen werden");
        assertTrue(((Map<?, ?>) ctx.gesendetesJson).containsKey("error"));
    }

    @Test
    void addArtikelMitPreisUnterMindestpreisLiefert400() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("0.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Preis unter 0.01 muss abgelehnt werden");
        assertEquals(0, fakeArtikelService.addArtikelCallCount);
    }

    @Test
    void addArtikelMitPreisUeberMaximalpreisLiefert400() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        ArtikelController testee = createTestee(fakeArtikelService, null);


        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("100000000.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Preis über 99999999.99 muss abgelehnt werden");
        assertEquals(0, fakeArtikelService.addArtikelCallCount);
    }

    @Test
    void addArtikelMitZuLangemBildLiefert400() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("10.00"), "b".repeat(501));
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Bild > 500 Zeichen muss abgelehnt werden");
        assertEquals(0, fakeArtikelService.addArtikelCallCount);
    }

    @Test
    void addArtikelMitLeeremNamenLiefert400() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        BenutzerDao benutzerDao = new EinfachesBenutzerDaoMock(Optional.of(
                new BenutzerEntity("Admin", "admin@example.com", "password", true)));
        ArtikelController testee = createTestee(fakeArtikelService, benutzerDao);

        AddArtikelRequest request = new AddArtikelRequest(
                "   ", new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus, "Leerer Name muss abgelehnt werden");
        assertEquals(0, fakeArtikelService.addArtikelCallCount);
    }

    @Test
    void addArtikelOhneAdminSessionLiefert401() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        BenutzerDao benutzerDao = new EinfachesBenutzerDaoMock(Optional.of(
                new BenutzerEntity("Admin", "admin@example.com", "password", false)));
        ArtikelController testee = createTestee(fakeArtikelService, benutzerDao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(401, ctx.gesetzterStatus, "Ohne Admin-Session muss 401 kommen");
        assertEquals(0, fakeArtikelService.addArtikelCallCount, "Ohne Admin darf das DAO nicht aufgerufen werden");
        assertEquals("Nur Administratoren dürfen Artikel anlegen.",
                ((Map<?, ?>) ctx.gesendetesJson).get("error"));
    }

    @Test
    void addArtikelMitNichtAdminSessionLiefert401() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList());
        BenutzerDao benutzerDao = new EinfachesBenutzerDaoMock(Optional.of(
                new BenutzerEntity("Admin", "admin@example.com", "password", false)));
        ArtikelController testee = createTestee(fakeArtikelService, benutzerDao);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(401, ctx.gesetzterStatus, "Mit isAdmin=false muss 401 kommen");
        assertEquals(0, fakeArtikelService.addArtikelCallCount, "Nicht-Admins dürfen das DAO nicht aufrufen");
    }

    @Test
    void addArtikelBeiExceptionLiefert500() throws Exception {
        FakeArtikelService fakeArtikelService = new FakeArtikelService(Collections.emptyList(), true);
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "iPhone 16 Pro", new BigDecimal("1299.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(500, ctx.gesetzterStatus);
        assertEquals("Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.",
                ctx.gesendetesResult, "Der API-Vertrag muss eine konkrete Fehlermeldung liefern");
        assertEquals(1, fakeArtikelService.addArtikelCallCount, "Das DAO muss genau einmal aufgerufen werden");
    }

    @Test
    void fetchAllArtikelRuftDaoGenauEinmalAuf() throws Exception {
        FakeArtikelService fakeArtikelService = getFakeArtikelServiceOneArtikel();
        ArtikelController testee = createTestee(fakeArtikelService, null);

        ArtikelContextMock ctx = new ArtikelContextMock();
        testee.ladeAlleArtikel.handle(ctx);

        assertEquals(200, ctx.gesetzterStatus);
        assertEquals(1, fakeArtikelService.callCount, "ladeAlleArtikel muss das DAO genau einmal verwenden");
    }

    @Test
    void addArtikelMitPreisGenauAmMindestpreisWirdAkzeptiert() throws Exception {
        FakeArtikelService fakeArtikelService = getFakeArtikelServiceOneArtikel();
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "Günstigster Artikel", new BigDecimal("0.01"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Preis genau am Mindestpreis (0.01) muss akzeptiert werden");
        assertEquals(1, fakeArtikelService.addArtikelCallCount);
    }

    @Test
    void addArtikelMitPreisGenauAmMaximalpreisWirdAkzeptiert() throws Exception {
        FakeArtikelService fakeArtikelService = getFakeArtikelServiceOneArtikel();
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "Teuerster Artikel", new BigDecimal("99999999.99"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Preis genau am Maximalpreis muss akzeptiert werden");
        assertEquals(1, fakeArtikelService.addArtikelCallCount);
    }

    @Test
    void addArtikelMitNameGenauAnMaxLaengeWirdAkzeptiert() throws Exception {
        FakeArtikelService fakeArtikelService = getFakeArtikelServiceOneArtikel();
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "a".repeat(255), new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Name mit genau 255 Zeichen muss noch akzeptiert werden");
    }

    @Test
    void addArtikelMitBildGenauAnMaxLaengeWirdAkzeptiert() throws Exception {
        FakeArtikelService fakeArtikelService = getFakeArtikelServiceOneArtikel();
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "Name", new BigDecimal("10.00"), "b".repeat(500));
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Bild mit genau 500 Zeichen muss noch akzeptiert werden");
    }

    @Test
    void addArtikelOhneBildWirdAkzeptiert() throws Exception {
        FakeArtikelService fakeArtikelService = getFakeArtikelServiceOneArtikel();
        ArtikelController testee = createTestee(fakeArtikelService, null);

        AddArtikelRequest request = new AddArtikelRequest(
                "Name", new BigDecimal("10.00"), null);
        ArtikelContextMock ctx = new ArtikelContextMock(request);
        ctx.sessionAttribute("userEmail", "admin@example.com");

        testee.erstelleNeuenArtikel.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus, "Bild ist laut validiere() optional (nur Länge wird geprüft, falls vorhanden)");
        assertEquals(1, fakeArtikelService.addArtikelCallCount);
    }

    private ArtikelController createTestee(ArtikelService artikelService, BenutzerDao benutzerDao) {
        if (artikelService == null) {
            artikelService = getFakeArtikelServiceOneArtikel();
        }
        if (benutzerDao == null) {
            benutzerDao = new EinfachesBenutzerDaoMock(Optional.of(
                    new BenutzerEntity("Admin", "admin@example.com", "password", true)));
        }
        return new ArtikelController(artikelService, benutzerDao);
    }

    private FakeArtikelService getFakeArtikelServiceOneArtikel() {
        return new FakeArtikelService(List.of(
                new ArtikelDto(42, "iPhone 15 Pro", new BigDecimal("1199.00"), "https://example.com/iphone.jpg")
        ));
    }
}
