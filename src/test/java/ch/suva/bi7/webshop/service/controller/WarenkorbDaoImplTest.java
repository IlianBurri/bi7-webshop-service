package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.dao.WarenkorbDao;
import ch.suva.bi7.webshop.service.dao.WarenkorbDaoImpl;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import ch.suva.bi7.webshop.service.mock.EntityManagerMock;
import jakarta.persistence.Tuple;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class WarenkorbDaoImplTest {

    @Test
    void warenkorbLesenLiefertItemsMitJoindaten() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        // Umschreiben: statt liste von "Tuple" das richtige Objekt vom Typ WarenkorbEintragEntity liefern !!!
        List<Tuple> tuples = List.of(
                tuple(Map.of("warenkorbItemId", 1, "userEmail", "test@somewhere.com", "artikelId", 5,
                        "menge", 3, "artikelName", "iPhone 15 Pro", "artikelPreis", new BigDecimal("1199.00"),
                        "artikelBild", "https://example.com/iphone.jpg")),
                tuple(Map.of("warenkorbItemId", 2, "userEmail", "test@somewhere.com", "artikelId", 6,
                        "menge", 1, "artikelName", "Samsung Galaxy S24", "artikelPreis", new BigDecimal("899.90"),
                        "artikelBild", "https://example.com/galaxy.jpg")));
        jpa.addResult(tuples);

        WarenkorbDao testee = new WarenkorbDaoImpl(jpa.factory());

        List<WarenkorbEintragEntity> items = testee.getWarenkorbNachBenutzer("test@somewhere.com");

        assertEquals(2, items.size());
        WarenkorbEintragEntity erster = items.get(0);
        assertEquals(1, erster.getWarenkorbItemId());
        assertEquals("test@somewhere.com", erster.getUserEmail());
        assertEquals(5, erster.getArtikelId());
        assertEquals(3, erster.getMenge());
        assertEquals("iPhone 15 Pro", erster.getArtikelName());
        assertEquals(new BigDecimal("1199.00"), erster.getArtikelPreis());
        assertEquals("https://example.com/iphone.jpg", erster.getArtikelBild());

        EntityManagerMock leer = new EntityManagerMock();
        assertTrue(new WarenkorbDaoImpl( leer.factory())
                .getWarenkorbNachBenutzer("test@somewhere.com").isEmpty());
    }

    @Test
    void artikelHinzufuegenNutztAtomaresUpsert() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();

        new WarenkorbDaoImpl(jpa.factory())
                .fuegeArtikelZuWarenkorbHinzu("test@somewhere.com", 5, 3);

        assertEquals(1, jpa.queries().size());
        SqlStatement upsert = jpa.queries().get(0);
        assertTrue(upsert.nativeQuery());
        assertTrue(upsert.sql().startsWith("INSERT INTO warenkorb_item"));
        assertTrue(upsert.sql().contains("ON DUPLICATE KEY UPDATE menge = menge + :menge"));
        assertEquals(Map.of("email", "test@somewhere.com", "artikelId", 5, "menge", 3),
                upsert.parameters());
        assertTrue(jpa.actions().contains("EntityTransaction.begin"));
        assertTrue(jpa.actions().contains("EntityTransaction.commit"));
    }

    @Test
    void warenkorbLesenJoinsArtikelUndFiltertNachEmail() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();

        new WarenkorbDaoImpl(jpa.factory()).getWarenkorbNachBenutzer("kunde@example.com");

        jpa.actions().contains("createQueryProxy: SELECT w FROM WarenkorbEintragEntity w WHERE w.userEmail = :email ORDER BY w.warenkorbItemId");
//        SqlStatement query = jpa.queries().get(0);
//        assertTrue(query.nativeQuery());
//        assertTrue(query.sql().contains("JOIN artikel"));
//        assertTrue(query.sql().contains("WHERE w.userEmail = :email"));
//        assertTrue(query.sql().contains("a.preis AS artikelPreis"));
//        assertEquals("kunde@example.com", query.parameters().get("email"));
    }

    @Test
    void mengeAktualisierenUndItemLoeschenErzeugenSql() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();

        assertTrue(new WarenkorbDaoImpl(jpa.factory()).aktualisiereMenge(42, 9));
        assertTrue(new WarenkorbDaoImpl(jpa.factory()).loescheWarenkorbEintrag(42));

        assertEquals(2, jpa.queries().size());
        assertEquals(Map.of("menge", 9, "id", 42), jpa.queries().get(0).parameters());
        assertTrue(jpa.queries().get(0).sql().startsWith("UPDATE warenkorb_item"));
        assertEquals(Map.of("id", 42), jpa.queries().get(1).parameters());
        assertTrue(jpa.queries().get(1).sql().startsWith("DELETE FROM warenkorb_item"));
    }

    @Test
    void mengeAktualisierenUndLoeschenMeldenFehlendeZeilen() throws Exception {
        EntityManagerMock jpa = new EntityManagerMock();
        jpa.addUpdateCount(0);
        jpa.addUpdateCount(0);
        WarenkorbDaoImpl testee = new WarenkorbDaoImpl(jpa.factory());

        assertFalse(testee.aktualisiereMenge(999, 3));
        assertFalse(testee.loescheWarenkorbEintrag(999));
    }

    @Test
    void konstruktorLehntNullAb() {
        assertThrows(IllegalArgumentException.class, () -> new WarenkorbDaoImpl(null));
    }

    private Tuple tuple(Map<String, Object> values) {
        return (Tuple) java.lang.reflect.Proxy.newProxyInstance(
                Tuple.class.getClassLoader(), new Class[]{Tuple.class}, (proxy, method, args) -> {
                    if (method.getName().equals("get")) {
                        Object value = values.get(args[0]);
                        if (args.length == 2 && value != null) {
                            return args[1] == Class.class ? value : value;
                        }
                        return value;
                    }
                    return null;
                });
    }
}
