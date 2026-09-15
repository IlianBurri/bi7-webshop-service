package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.List;

public class ArtikelDaoImpl implements ArtikelDao {

    private final EntityManagerFactory entityManagerFactory;

    public ArtikelDaoImpl(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory == null) {
            throw new IllegalArgumentException("entityManagerFactory must not be null");
        }
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<ArtikelEntity> getAllArtikel() throws Exception {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            return em.createQuery("SELECT a FROM ArtikelEntity a", ArtikelEntity.class).getResultList();
        }
    }

    @Override
    public ArtikelEntity erstelleNeuenArtikel(String name, BigDecimal preis, String bild) throws DaoException {
        EntityTransaction transaction = null;
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            transaction = em.getTransaction();
            transaction.begin();

            ArtikelEntity artikel = new ArtikelEntity(name, preis, bild);
            em.persist(artikel);

            transaction.commit();
            return artikel;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new DaoException("Fehler beim Speichern des Artikels", e);
        }
    }
}
