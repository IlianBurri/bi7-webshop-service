package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.BestellungDao;
import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbItemEntity;
import ch.suva.bi7.webshop.service.mock.EinfacherContextMock;
import io.javalin.http.Context;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BestellungControllerTest {

    private static final String TEST_EMAIL = "checkout.test@example.com";

    @Test
    void checkoutLiefertBestellungIdGesamtpreisUndStatus() throws Exception {
        List<WarenkorbItemEntity> warenkorb = List.of(
                new WarenkorbItemEntity(1, TEST_EMAIL, 5, 2, "iPhone 15 Pro", new BigDecimal("1199.00"), "bild"),
                new WarenkorbItemEntity(2, TEST_EMAIL, 6, 1, "Samsung Galaxy S24", new BigDecimal("899.90"), "bild"));
        FakeBestellungDao bestellungDao = new FakeBestellungDao();
        CheckoutWarenkorbDao warenkorbDao = new CheckoutWarenkorbDao(warenkorb);
        BestellungContextMock ctx = new BestellungContextMock(Map.of("adressId", 3));
        ctx.sessionAttribute("userEmail", TEST_EMAIL);

        new BestellungController(bestellungDao, warenkorbDao).createBestellung.handle(ctx);

        assertEquals(201, ctx.gesetzterStatus);
        assertEquals(new BigDecimal("3297.90"), bestellungDao.letzterGesamtpreis,
                "Der berechnete Gesamtpreis (2x 1199.00 + 1x 899.90) muss an die DAO gehen");
        assertEquals(TEST_EMAIL, bestellungDao.letzterUserEmail);
        assertEquals(3, bestellungDao.letzteAdressId);
        assertEquals(2, bestellungDao.letzteItems.size());

        Map<?, ?> antwort = (Map<?, ?>) ctx.gesendetesJson;
        assertEquals(5, antwort.get("bestellungId"));
        assertEquals(new BigDecimal("3297.90"), antwort.get("gesamtpreis"),
                "Die Checkout-Antwort muss den gespeicherten Gesamtpreis enthalten");
        assertEquals("BEZAHLT", antwort.get("status"));
    }

    @Test
    void checkoutOhneLoginLiefert401() throws Exception {
        FakeBestellungDao bestellungDao = new FakeBestellungDao();
        CheckoutWarenkorbDao warenkorbDao = new CheckoutWarenkorbDao(List.of());
        BestellungContextMock ctx = new BestellungContextMock(Map.of("adressId", 3));

        new BestellungController(bestellungDao, warenkorbDao).createBestellung.handle(ctx);

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

        new BestellungController(bestellungDao, warenkorbDao).createBestellung.handle(ctx);

        assertEquals(400, ctx.gesetzterStatus);
        assertEquals("Warenkorb ist leer.", ctx.gesendetesResult);
        assertFalse(bestellungDao.wurdeAufgerufen, "Bei leerem Warenkorb darf keine Bestellung erstellt werden");
    }
}

class FakeBestellungDao implements BestellungDao {

    boolean wurdeAufgerufen = false;
    String letzterUserEmail;
    int letzteAdressId;
    BigDecimal letzterGesamtpreis;
    List<WarenkorbItemEntity> letzteItems;

    @Override
    public int createBestellungWithItems(String userEmail, int adressId, BigDecimal gesamtpreis, List<WarenkorbItemEntity> items) {
        wurdeAufgerufen = true;
        letzterUserEmail = userEmail;
        letzteAdressId = adressId;
        letzterGesamtpreis = gesamtpreis;
        letzteItems = items;
        return 5;
    }

    @Override
    public Optional<BestellungEntity> getBestellungById(int bestellungId) {
        return Optional.empty();
    }

    @Override
    public List<BestellungEntity> getBestellungenByUserEmail(String userEmail) {
        return List.of();
    }
}

class CheckoutWarenkorbDao implements WarenkorbDao {

    private final List<WarenkorbItemEntity> items;

    CheckoutWarenkorbDao(List<WarenkorbItemEntity> items) {
        this.items = items;
    }

    @Override
    public List<WarenkorbItemEntity> getWarenkorbByUser(String email) {
        return items;
    }

    @Override
    public void addArtikelToWarenkorb(String email, int artikelId, int menge) {
    }

    @Override
    public boolean updateMenge(int warenkorbItemId, int menge) {
        return true;
    }

    @Override
    public boolean deleteWarenkorbItem(int warenkorbItemId) {
        return true;
    }

    @Override
    public boolean clearWarenkorbByUser(String email) {
        return true;
    }
}

class BestellungContextMock extends EinfacherContextMock {

    String gesendetesResult;

    BestellungContextMock(Object body) {
        super(body);
    }

    @Override
    public Context result(String result) {
        this.gesendetesResult = result;
        return this;
    }
}
