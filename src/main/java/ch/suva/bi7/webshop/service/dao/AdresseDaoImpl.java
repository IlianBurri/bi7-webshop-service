package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.AdresseEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class AdresseDaoImpl implements AdresseDao {

    private final EntityManagerFactory entityManagerFactory;

    public AdresseDaoImpl(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory == null) {
            throw new IllegalArgumentException("entityManagerFactory must not be null");
        }
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public List<AdresseEntity> ladeAdressenNachBenutzerEmail(String email) throws DaoException {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT a FROM AdresseEntity a WHERE a.userEmail = :email ORDER BY a.adressId DESC",
                            AdresseEntity.class)
                    .setParameter("email", email)
                    .getResultList();
        } catch (Exception e) {
            throw new DaoException("Fehler beim Abrufen der Adressen", e);
        }
    }

    @Override
    public AdresseEntity insert(AdresseEntity adresse) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(adresse);
            tx.commit();
            return adresse;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Speichern der Adresse", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean aktualisiere(int adressId, AdresseEntity adresse) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            int updatedRows = em.createQuery(
                            "UPDATE AdresseEntity a SET " +
                                    "a.userEmail = :email, " +
                                    "a.vorname = :vorname, " +
                                    "a.nachname = :nachname, " +
                                    "a.strasse = :strasse, " +
                                    "a.plz = :plz, " +
                                    "a.ort = :ort, " +
                                    "a.land = :land " +
                                    "WHERE a.adressId = :id")
                    .setParameter("email", adresse.getUserEmail())
                    .setParameter("vorname", adresse.getVorname())
                    .setParameter("nachname", adresse.getNachname())
                    .setParameter("strasse", adresse.getStrasse())
                    .setParameter("plz", adresse.getPlz())
                    .setParameter("ort", adresse.getOrt())
                    .setParameter("land", adresse.getLand())
                    .setParameter("id", adressId)
                    .executeUpdate();

            tx.commit();
            return updatedRows > 0;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Aktualisieren der Adresse", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean loesche(int adressId) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            AdresseEntity adresse = em.find(AdresseEntity.class, adressId);
            if (adresse != null) {
                em.remove(adresse);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new DaoException("Fehler beim Löschen der Adresse", e);
        } finally {
            em.close();
        }
    }

    @Override
    public boolean existiertIdentischeAdresse(AdresseEntity adresse) throws DaoException {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            Long count = em.createQuery(
                            "SELECT COUNT(a) FROM AdresseEntity a WHERE a.userEmail = :email " +
                                    "AND a.vorname = :vorname AND a.nachname = :nachname AND a.strasse = :strasse " +
                                    "AND a.plz = :plz AND a.ort = :ort AND a.land = :land", Long.class)
                    .setParameter("email", adresse.getUserEmail())
                    .setParameter("vorname", adresse.getVorname())
                    .setParameter("nachname", adresse.getNachname())
                    .setParameter("strasse", adresse.getStrasse())
                    .setParameter("plz", adresse.getPlz())
                    .setParameter("ort", adresse.getOrt())
                    .setParameter("land", adresse.getLand())
                    .getSingleResult();

            return count > 0;
        } catch (Exception e) {
            throw new DaoException("Fehler beim Prüfen auf identische Adresse", e);
        }
    }
}