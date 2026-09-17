package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

import java.math.BigDecimal;
import java.util.ArrayList;
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
            String sql = "SELECT w.warenkorbItemId, w.userEmail, w.artikelId, w.menge, " +
                    "a.name AS artikelName, a.preis AS artikelPreis, a.bild AS artikelBild " +
                    "FROM warenkorb_item w " +
                    "JOIN artikel a ON w.artikelId = a.artikelId " +
                    "WHERE w.userEmail = :email";

            List<Tuple> results = em.createNativeQuery(sql, Tuple.class)
                    .setParameter("email", email)
                    .getResultList();

            List<WarenkorbEintragEntity> warenkorbEintragEntityList = new ArrayList<>();
            for (Tuple tuple : results) {
                Integer warenkorbItemId = tuple.get("warenkorbItemId", Number.class).intValue();
                String userEmail = tuple.get("userEmail", String.class);
                Integer artikelId = tuple.get("artikelId", Number.class).intValue();
                Integer menge = tuple.get("menge", Number.class).intValue();
                String artikelName = tuple.get("artikelName", String.class);
                BigDecimal artikelPreis = tuple.get("artikelPreis", BigDecimal.class);
                String artikelBild = tuple.get("artikelBild", String.class);

                warenkorbEintragEntityList.add(new WarenkorbEintragEntity(
                        warenkorbItemId, userEmail, artikelId, menge,
                        artikelName, artikelPreis, artikelBild
                ));
            }
            return warenkorbEintragEntityList;
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

            String sql = "INSERT INTO warenkorb_item (userEmail, artikelId, menge) VALUES (:email, :artikelId, :menge) " +
                    "ON DUPLICATE KEY UPDATE menge = menge + :menge";

            em.createNativeQuery(sql)
                    .setParameter("email", email)
                    .setParameter("artikelId", artikelId)
                    .setParameter("menge", menge)
                    .executeUpdate();

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