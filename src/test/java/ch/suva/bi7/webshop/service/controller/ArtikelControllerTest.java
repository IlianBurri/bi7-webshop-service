package ch.suva.bi7.webshop.service.controller;

import ch.suva.bi7.webshop.service.model.Artikel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ArtikelControllerTest {

    static class FakeArtikelDao implements ArtikelDao {

        private final List<Artikel> artikel;

        FakeArtikelDao(List<Artikel> artikel) {
            this.artikel = artikel;
        }

        @Override
        public List<Artikel> getAllArtikel() {
            return artikel;
        }
    }


    static class FehlerArtikelDao implements ArtikelDao {

        @Override
        public List<Artikel> getAllArtikel() throws Exception {
            throw new Exception("Datenbank Fehler");
        }
    }


    @Test
    void mehrereArtikelWerdenZurueckgegeben() throws Exception {

        ArtikelDao dao = new FakeArtikelDao(List.of(
                new Artikel(
                        1,
                        "iPhone 15 Pro",
                        new BigDecimal("1199.00"),
                        "https://imgs.search.brave.com/XKzj-Ry1DHNPSKMfAu3qWuKp_PdZCmUA9_yPjrLtfP8/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9zczcu/dnp3LmNvbS9pcy9p/bWFnZS9WZXJpem9u/V2lyZWxlc3MvYXBw/bGUtaXBob25lLTE1/LXByby0xdGItbmF0/dXJhbC10aXRhbml1/bS1tdHU1M2xsLWEt/YT93aWQ9NDAwJmhl/aT00MDAmZm10PXdl/YnAtYWxwaGE"
                ),
                new Artikel(
                        2,
                        "Samsung Galaxy S24",
                        new BigDecimal("899.90"),
                        "https://imgs.search.brave.com/BkadkX__5a26LuCKGPBUVS5kY4cRhKoh2dXmvCeXYgk/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tLWNk/bi5waG9uZWFyZW5h/LmNvbS9pbWFnZXMv/cGhvbmVzLzg0Mzg5/LTM1MC9TYW1zdW5n/LUdhbGF4eS1TMjQu/d2VicD93PTE"
                )
        ));

        List<Artikel> artikel = dao.getAllArtikel();

        assertNotNull(artikel);
        assertEquals(2, artikel.size());

        assertEquals("iPhone 15 Pro", artikel.get(0).name);
        assertEquals("Samsung Galaxy S24", artikel.get(1).name);
    }


    @Test
    void ArtikelWirdKorrektZurückgegeben() throws Exception {

        ArtikelDao dao = new FakeArtikelDao(List.of(
                new Artikel(
                        1,
                        "iPhone 15 Pro",
                        new BigDecimal("1199.00"),
                        "https://imgs.search.brave.com/XKzj-Ry1DHNPSKMfAu3qWuKp_PdZCmUA9_yPjrLtfP8/rs:fit:500:0:1:0/g:ce/aHR0cHM6Ly9zczcu/dnp3LmNvbS9pcy9p/bWFnZS9WZXJpem9u/V2lyZWxlc3MvYXBw/bGUtaXBob25lLTE1/LXByby0xdGItbmF0/dXJhbC10aXRhbml1/bS1tdHU1M2xsLWEt/YT93aWQ9NDAwJmhl/aT00MDAmZm10PXdl/YnAtYWxwaGE"
                )
        ));

        List<Artikel> artikel = dao.getAllArtikel();

        assertEquals(1, artikel.size());
        assertEquals(1, artikel.get(0).artikelId);
        assertEquals("iPhone 15 Pro", artikel.get(0).name);
    }


    @Test
    void KeineArtikelVorhandenWirdLeereListeZurückgegeben() throws Exception {

        ArtikelDao dao = new FakeArtikelDao(Collections.emptyList());

        List<Artikel> artikel = dao.getAllArtikel();

        assertNotNull(artikel);
        assertTrue(artikel.isEmpty());
    }


    @Test
    void datenbankFehlerWirdBehandelt() {

        ArtikelDao dao = new FehlerArtikelDao();

        assertThrows(Exception.class, dao::getAllArtikel);
    }
}
