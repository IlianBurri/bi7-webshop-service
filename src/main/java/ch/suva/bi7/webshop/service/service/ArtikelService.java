package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.mapper.ArtikelMapper;
import ch.suva.bi7.webshop.service.model.ArtikelDto;

import java.math.BigDecimal;
import java.util.List;

public class ArtikelService {

    private ArtikelDao artikelDao = null;

    public ArtikelService(ArtikelDao artikelDao) {
        if (artikelDao == null) {
            throw new IllegalArgumentException("entityManagerFactory must not be null");
        }
        this.artikelDao = artikelDao;
    }

    public List<ArtikelDto> getAllArtikel() throws Exception {
        List<ArtikelEntity> artikelEntityList = artikelDao.getAllArtikel();
        return artikelEntityList.stream()
                .map(ArtikelMapper::toDto)
                .toList();
    }

    public ArtikelDto erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws Exception {
        ArtikelEntity artikelEntity = artikelDao.erstelleNeuenArtikel(name, preis, bild);
        return ArtikelMapper.toDto(artikelEntity);
    }
}
