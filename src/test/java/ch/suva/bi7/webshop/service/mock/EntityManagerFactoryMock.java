package ch.suva.bi7.webshop.service.mock;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.metamodel.Metamodel;

import java.util.Map;

public class EntityManagerFactoryMock implements EntityManagerFactory {

    private final EntityManager entityManager;

    public EntityManagerFactoryMock(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EntityManager createEntityManager() {
        return entityManager;
    }

    @Override
    public EntityManager createEntityManager(Map map) {
        return entityManager;
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return entityManager;
    }

    @Override
    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return entityManager;
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return null;
    }

    @Override
    public Metamodel getMetamodel() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public void close() {

    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of();
    }

    @Override
    public Cache getCache() {
        return null;
    }

    @Override
    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return null;
    }

    @Override
    public void addNamedQuery(String name, Query query) {

    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {

    }
}
