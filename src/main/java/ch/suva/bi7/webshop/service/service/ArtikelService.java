package ch.suva.bi7.webshop.service.service;

import ch.suva.bi7.webshop.service.dao.ArtikelDao;
import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.mapper.ArtikelMapper;
import ch.suva.bi7.webshop.service.model.ArtikelDto;

import java.math.BigDecimal;
import java.util.List;

public class ArtikelService {

    private final ArtikelDao artikelDao;

    public ArtikelService(ArtikelDao artikelDao) {
        if (artikelDao == null) {
            throw new IllegalArgumentException("artikelDao must not be null");
        }
        this.artikelDao = artikelDao;
    }

    public List<ArtikelDto> getAllArtikel() {
        try {
            List<ArtikelEntity> artikelEntityList = artikelDao.getAllArtikel();
            return artikelEntityList.stream()
                    .map(ArtikelMapper::toDto)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArtikelDto getArtikelById(Integer artikelId) {
        try {
            ArtikelEntity artikelEntity = artikelDao.getArtikelById(artikelId);
            if (artikelEntity == null) {
                return null;
            }
            return ArtikelMapper.toDto(artikelEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArtikelDto erstelleNeuenArtikel(String name, BigDecimal preis, String bild) {
        try {
            ArtikelEntity artikelEntity = artikelDao.erstelleNeuenArtikel(name, preis, bild);
            return ArtikelMapper.toDto(artikelEntity);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
