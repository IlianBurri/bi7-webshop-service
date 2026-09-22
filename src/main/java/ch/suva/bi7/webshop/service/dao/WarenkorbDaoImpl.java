package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class WarenkorbDaoImpl implements WarenkorbDao {

    private final EntityManagerFactory entityManagerFactory;

    public WarenkorbDaoImpl(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory == null) {
            throw new IllegalArgumentException("entityManagerFactory must not be null");
        }
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<WarenkorbEintragEntity> getWarenkorbNachBenutzer(String email) throws DaoException {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT w FROM WarenkorbEintragEntity w " +
                                    "WHERE w.userEmail = :email " +
                                    "ORDER BY w.warenkorbItemId",
                            WarenkorbEintragEntity.class)
                    .setParameter("email", email)
                    .getResultList();
        } catch (Exception e) {
            throw new DaoException("Fehler beim Abrufen des Warenkorbs", e);
        }
    }

    @Override
    public void fuegeArtikelZuWarenkorbHinzu(String email, int artikelId, int menge) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            List<WarenkorbEintragEntity> vorhandeneEintraege = em.createQuery(
                            "SELECT w FROM WarenkorbEintragEntity w " +
                                    "WHERE w.userEmail = :email AND w.artikelId = :artikelId",
                            WarenkorbEintragEntity.class)
                    .setParameter("email", email)
                    .setParameter("artikelId", artikelId)
                    .getResultList();

            if (vorhandeneEintraege.isEmpty()) {
                em.persist(new WarenkorbEintragEntity(email, artikelId, menge));
            } else {
                vorhandeneEintraege.get(0).erhoeheMenge(menge);
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Hinzufügen zum Warenkorb", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean aktualisiereMenge(int warenkorbItemId, int menge) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            String sql = "UPDATE warenkorb_item SET menge = :menge WHERE warenkorbItemId = :id";
            int updatedRows = em.createNativeQuery(sql)
                    .setParameter("menge", menge)
                    .setParameter("id", warenkorbItemId)
                    .executeUpdate();

            tx.commit();
            return updatedRows > 0;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Aktualisieren der Menge", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean loescheWarenkorbEintrag(int warenkorbItemId) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            String sql = "DELETE FROM warenkorb_item WHERE warenkorbItemId = :id";
            int deletedRows = em.createNativeQuery(sql)
                    .setParameter("id", warenkorbItemId)
                    .executeUpdate();

            tx.commit();
            return deletedRows > 0;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Löschen des Warenkorb-Items", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean leereWarenkorbNachBenutzer(String email) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            String sql = "DELETE FROM warenkorb_item WHERE userEmail = :email";
            int deletedRows = em.createNativeQuery(sql)
                    .setParameter("email", email)
                    .executeUpdate();

            tx.commit();
            return deletedRows > 0;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Leeren des Warenkorbs für den Benutzer " + email, e);
        } finally {
            em.close();
        }
    }
}
