package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.BenutzerEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;
import java.util.Optional;

public class BenutzerDaoImpl implements BenutzerDao {

    private final EntityManagerFactory entityManagerFactory;

    public BenutzerDaoImpl(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory == null) {
            throw new IllegalArgumentException("entityManagerFactory must not be null");
        }
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public Optional<BenutzerEntity> holeBenutzerNachEMail(String email) {
        if (email == null || email.isBlank()) {
            return Optional.empty();
        }

        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            BenutzerEntity benutzer = em.createQuery(
                            "SELECT b FROM BenutzerEntity b WHERE b.email = :email", BenutzerEntity.class)
                    .setParameter("email", email)
                    .getSingleResult();

            return Optional.of(benutzer);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<BenutzerEntity> holeAlleBenutzernamen() {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            return em.createQuery("SELECT b FROM BenutzerEntity b", BenutzerEntity.class).getResultList();
        }
    }

    @Override
    public void speichereBenutzer(BenutzerEntity neuerBenutzer) throws DaoException {
        if (neuerBenutzer == null) {
            throw new IllegalArgumentException("neuerBenutzer must not be null");
        }

        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(neuerBenutzer);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Speichern des Benutzers: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
