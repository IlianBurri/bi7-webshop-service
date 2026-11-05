package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.LagerbestandException;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import ch.suva.bi7.webshop.service.mock.ArtikelDaoMock;
import ch.suva.bi7.webshop.service.mock.BestellungContextMock;
import ch.suva.bi7.webshop.service.mock.CheckoutWarenkorbDao;
import ch.suva.bi7.webshop.service.mock.FakeArtikelDao;
import ch.suva.bi7.webshop.service.mock.FakeBestellungDao;
import ch.suva.bi7.webshop.service.model.CheckoutResponse;
import ch.suva.bi7.webshop.service.service.BestellungService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BestellungControllerTest {

    private static final String TEST_EMAIL = "checkout.test@example.com";

    @Test
    void checkoutLiefertBestellungIdGesamtpreisUndStatus() throws Exception {
        ArtikelEntity artikel1 = EntityHelper.createArtikelEntity(5,"Iphone 15 pro",new BigDecimal("1199.00"),"bild.png");
        ArtikelEntity artikel2 = EntityHelper.createArtikelEntity(6,"Samsung Galaxy S24",new BigDecimal("899.90"),"bild.png");

        ArtikelDaoMock artikelDao = new ArtikelDaoMock(List.of(artikel1, artikel2));

        List<WarenkorbEintragEntity> warenkorb = List.of(
                EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 2),
                EntityHelper.createWarenkorbEintragEntity(2, TEST_EMAIL, 6, 1));
        FakeBestellungDao bestellungDao = new FakeBestellungDao();
        CheckoutWarenkorbDao warenkorbDao = new CheckoutWarenkorbDao(warenkorb);
        BestellungContextMock ctx = new BestellungContextMock(Map.of("adressId", 3));
        ctx.sessionAttribute("userEmail", TEST_EMAIL);

        new BestellungController(new BestellungService(bestellungDao, warenkorbDao, artikelDao)).erstelleBestellung.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus);
        assertEquals(new BigDecimal("3297.90"), bestellungDao.letzterGesamtpreis,
                "Der berechnete Gesamtpreis (2x 1199.00 + 1x 899.90) muss an die DAO gehen");
        assertEquals(TEST_EMAIL, bestellungDao.letzterUserEmail);
        assertEquals(3, bestellungDao.letzteAdressId);
        assertEquals(2, bestellungDao.letzteItems.size());
        assertEquals(new BigDecimal("1199.00"), bestellungDao.letzteArtikelPreise.get(5));
        assertEquals(new BigDecimal("899.90"), bestellungDao.letzteArtikelPreise.get(6));

        CheckoutResponse antwort = (CheckoutResponse) ctx.gesendetesJson;
        assertEquals(5, antwort.bestellungId);
        assertEquals(new BigDecimal("3297.90"), antwort.gesamtpreis,
                "Die Checkout-Antwort muss den gespeicherten Gesamtpreis enthalten");
        assertEquals("BEZAHLT", antwort.status);
        assertFalse(warenkorbDao.wurdeGeleert,
                "Der Service darf den Warenkorb nicht separat leeren, das passiert in der Bestell-Transaktion im DAO");
    }

    @Test
    void checkoutMitZuWenigLagerbestandLiefert409MitArtikelname() throws Exception {
        ArtikelEntity artikel1 = EntityHelper.createArtikelEntity(5, "Iphone 15 pro", new BigDecimal("1199.00"), "bild.png");
        ArtikelEntity artikel2 = EntityHelper.createArtikelEntity(6, "Samsung Galaxy S24", new BigDecimal("899.90"), "bild.png");
        FakeBestellungDao bestellungDao = new FakeBestellungDao();
        bestellungDao.zuWerfendeException = new LagerbestandException(6, "Artikel 6 ist nicht in der Menge 1 an Lager.");
        CheckoutWarenkorbDao warenkorbDao = new CheckoutWarenkorbDao(List.of(
                EntityHelper.createWarenkorbEintragEntity(1, TEST_EMAIL, 5, 2),
                EntityHelper.createWarenkorbEintragEntity(2, TEST_EMAIL, 6, 1)));
        BestellungContextMock ctx = new BestellungContextMock(Map.of("adressId", 3));
        ctx.sessionAttribute("userEmail", TEST_EMAIL);

        new BestellungController(new BestellungService(bestellungDao, warenkorbDao,
                new ArtikelDaoMock(List.of(artikel1, artikel2)))).erstelleBestellung.handle(ctx);

        assertEquals(409, ctx.gesetzterStatus, "Zu wenig Lagerbestand ist ein Konflikt (409), kein Serverfehler");
        Map<?, ?> antwort = (Map<?, ?>) ctx.gesendetesJson;
        assertEquals(6, antwort.get("artikelId"));
        assertTrue(((String) antwort.get("error")).contains("Samsung Galaxy S24"),
                "Die Fehlermeldung für den User muss den Artikelnamen enthalten");
        assertFalse(warenkorbDao.wurdeGeleert, "Bei Abbruch bleibt der Warenkorb erhalten");
    }

    @Test
    void checkoutOhneLoginLiefert401() throws Exception {
        FakeBestellungDao bestellungDao = new FakeBestellungDao();
        CheckoutWarenkorbDao warenkorbDao = new CheckoutWarenkorbDao(List.of());
        BestellungContextMock ctx = new BestellungContextMock(Map.of("adressId", 3));

        new BestellungController(new BestellungService(bestellungDao, warenkorbDao, new FakeArtikelDao())).erstelleBestellung.handle(ctx);

        assertEquals(401, ctx.gesetzterStatus);
        assertEquals("Nicht eingeloggt.", ctx.gesendetesResult);
        assertFalse(bestellungDao.wurdeAufgerufen, "Ohne Login darf keine Bestellung erstellt werden");
    }

    @Test
    void checkoutMitLeeremWarenkorbLiefert400() throws Exception {
        FakeBestellungDao bestellungDao = new FakeBestellungDao();
        CheckoutWarenkorbDao warenkorbDao = new CheckoutWarenkorbDao(List.of());
        BestellungContextMock ctx = new BestellungContextMock(Map.of("adressId", 3));
        ctx.sessionAttribute("userEmail", TEST_EMAIL);

        new BestellungController(new BestellungService(bestellungDao, warenkorbDao, new FakeArtikelDao())).erstelleBestellung.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
        assertEquals("Warenkorb ist leer.", ctx.gesendetesResult);
        assertFalse(bestellungDao.wurdeAufgerufen, "Bei leerem Warenkorb darf keine Bestellung erstellt werden");
    }
}
