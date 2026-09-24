package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.BestellPositionEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungEntity;
import ch.suva.bi7.webshop.service.db.entity.BestellungStatusEnum;
import ch.suva.bi7.webshop.service.db.entity.WarenkorbEintragEntity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public class BestellungDaoImpl implements BestellungDao {

    private static final Logger logger = LoggerFactory.getLogger(BestellungDaoImpl.class);

    private final EntityManagerFactory entityManagerFactory;

    public BestellungDaoImpl(EntityManagerFactory entityManagerFactory) {
        if (entityManagerFactory == null) {
            throw new IllegalArgumentException("entityManagerFactory must not be null");
        }
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public int erstelleBestellungMitWarenkorbItems(String userEmail, int adressId, BigDecimal gesamtpreis,
                                                   List<WarenkorbEintragEntity> warenkorbEintragEntityList) throws DaoException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            BestellungEntity bestellung = new BestellungEntity(
                    userEmail,
                    adressId,
                    gesamtpreis,
                    BestellungStatusEnum.BEZAHLT,
                    new Timestamp(System.currentTimeMillis())
            );
            em.persist(bestellung);
            em.flush();

            for (WarenkorbEintragEntity eintrag : warenkorbEintragEntityList) {
                BestellPositionEntity bestellPosition = new BestellPositionEntity(
                        bestellung.getBestellungId(),
                        eintrag.getArtikelId(),
                        eintrag.getMenge(),
                        eintrag.getArtikelPreis()
                );
                em.persist(bestellPosition);

            }
            tx.commit();
            return bestellung.getBestellungId();

        } catch (Exception e) {
            if (tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception rbEx) {
                    logger.warn("Rollback nach fehlgeschlagener Bestellung fehlgeschlagen", rbEx);
                }
            }
            throw new DaoException("Fehler beim Erstellen der Bestellung für Benutzer: " + userEmail, e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<BestellungEntity> holeBestellungNachId(int bestellungId) throws DaoException {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            BestellungEntity bestellung = em.find(BestellungEntity.class, bestellungId);
            return Optional.ofNullable(bestellung);
        } catch (Exception e) {
            throw new DaoException("Fehler beim Abrufen der Bestellung mit ID: " + bestellungId, e);
        }
    }

    @Override
    public List<BestellungEntity> getBestellungenNachBenutzerEmail(String userEmail) throws DaoException {
        try (EntityManager em = entityManagerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT b FROM BestellungEntity b WHERE b.userEmail = :email ORDER BY b.bestellungId DESC",
                            BestellungEntity.class)
                    .setParameter("email", userEmail)
                    .getResultList();
        } catch (Exception e) {
            throw new DaoException("Fehler beim Abrufen der Bestellungen für Benutzer: " + userEmail, e);
        }
    }
}
