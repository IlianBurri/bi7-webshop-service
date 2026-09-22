package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.db.entity.ArtikelEntity;
import ch.suva.bi7.webshop.service.helper.EntityHelper;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.Metamodel;

import java.util.List;
import java.util.Map;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.lang.reflect.Proxy;

public class EntityManagerMock implements EntityManager {

    private EntityTransaction entityTransaction;
    private List<String> actions;
    private int generierterKey;
    private final Deque<Object> results = new ArrayDeque<>();
    private final Deque<Integer> updateCounts = new ArrayDeque<>();
    private final List<Object> persistedEntities = new ArrayList<>();
    private RuntimeException queryException;
    private RuntimeException updateException;
    private final Map<FindKey, Object> findResults = new java.util.HashMap<>();

    public EntityManagerMock() {
        List<String> sharedActions = new ArrayList<>();
        this.entityTransaction = new EntityTransactionMock(sharedActions);
        this.actions = sharedActions;
    }

    @Override
    public void persist(Object entity) {
        persistedEntities.add(entity);
        if (updateException != null) {
            throw updateException;
        }
        if (generierterKey > 0) {
            try {
                String idName = entity instanceof ArtikelEntity ? "artikelId" : "bestellungId";
                java.lang.reflect.Field id = entity.getClass().getDeclaredField(idName);
                id.setAccessible(true);
                id.set(entity, generierterKey);
            } catch (ReflectiveOperationException ignored) {
                if (entity instanceof ArtikelEntity artikel) {
                    EntityHelper.setArtikelId(artikel, generierterKey);
                }
            }
        }
        actions.add("EntityManager.persist");
    }

    @Override
    public <T> T merge(T entity) {
        return null;
    }

    @Override
    public void remove(Object entity) {

    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return (T) findResults.get(new FindKey(entityClass, primaryKey));
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return null;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) {
        return null;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, Map<String, Object> properties) {
        return null;
    }

    @Override
    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        return null;
    }

    @Override
    public void flush() {
        actions.add("EntityManager.flush");
    }

    @Override
    public void setFlushMode(FlushModeType flushMode) {

    }

    @Override
    public FlushModeType getFlushMode() {
        return null;
    }

    @Override
    public void lock(Object entity, LockModeType lockMode) {

    }

    @Override
    public void lock(Object entity, LockModeType lockMode, Map<String, Object> properties) {

    }

    @Override
    public void refresh(Object entity) {

    }

    @Override
    public void refresh(Object entity, Map<String, Object> properties) {

    }

    @Override
    public void refresh(Object entity, LockModeType lockMode) {

    }

    @Override
    public void refresh(Object entity, LockModeType lockMode, Map<String, Object> properties) {

    }

    @Override
    public void clear() {

    }

    @Override
    public void detach(Object entity) {

    }

    @Override
    public boolean contains(Object entity) {
        return false;
    }

    @Override
    public LockModeType getLockMode(Object entity) {
        return null;
    }

    @Override
    public void setProperty(String propertyName, Object value) {

    }

    @Override
    public Map<String, Object> getProperties() {
        return Map.of();
    }

    @Override
    public Query createQuery(String qlString) {
        return createQueryProxy();
    }

    @Override
    public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
        return null;
    }

    @Override
    public Query createQuery(CriteriaUpdate updateQuery) {
        return null;
    }

    @Override
    public Query createQuery(CriteriaDelete deleteQuery) {
        return null;
    }

    @Override
    public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
        return (TypedQuery<T>) createQueryProxy();
    }

    @Override
    public Query createNamedQuery(String name) {
        return null;
    }

    @Override
    public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
        return null;
    }

    @Override
    public Query createNativeQuery(String sqlString) {
        return createQueryProxy();
    }

    @Override
    public Query createNativeQuery(String sqlString, Class resultClass) {
        return createQueryProxy();
    }

    @Override
    public Query createNativeQuery(String sqlString, String resultSetMapping) {
        return null;
    }

    @Override
    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        return null;
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        return null;
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class... resultClasses) {
        return null;
    }

    @Override
    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String... resultSetMappings) {
        return null;
    }

    @Override
    public void joinTransaction() {

    }

    @Override
    public boolean isJoinedToTransaction() {
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> cls) {
        return null;
    }

    @Override
    public Object getDelegate() {
        return null;
    }

    @Override
    public void close() {
        actions.add("EntityManager.close");
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public EntityTransaction getTransaction() {
        return entityTransaction;
    }

    public EntityManagerFactory factory() {
        return new EntityManagerFactoryMock(this);
    }

    public void addResult(Object result) {
        results.add(result);
    }

    public void addUpdateCount(int updateCount) {
        updateCounts.add(updateCount);
    }

    public void generatedKey(int key) {
        generierterKey = key;
    }

    public void queryException(RuntimeException exception) {
        queryException = exception;
    }

    public void updateException(RuntimeException exception) {
        updateException = exception;
    }

    public List<String> actions() {
        return actions;
    }

    public List<Object> persistedEntities() {
        return persistedEntities;
    }

    public void find(Class<?> type, Object id, Object result) {
        findResults.put(new FindKey(type, id), result);
    }

    private Query createQueryProxy() {
        actions.add("createQueryProxy");
        final Query[] holder = new Query[1];
        holder[0] = (Query) Proxy.newProxyInstance(Query.class.getClassLoader(),
                new Class<?>[]{Query.class, TypedQuery.class}, (object, method, args) -> {
                    switch (method.getName()) {
                        case "setParameter" -> {
                            return holder[0];
                        }
                        case "getResultList" -> {
                            if (queryException != null) throw queryException;
                            return results.isEmpty() ? List.of() : results.removeFirst();
                        }
                        case "getSingleResult" -> {
                            if (queryException != null) throw queryException;
                            Object result = results.isEmpty() ? null : results.removeFirst();
                            if (result instanceof RuntimeException exception) throw exception;
                            return result;
                        }
                        case "executeUpdate" -> {
                            if (updateException != null) throw updateException;
                            return updateCounts.isEmpty() ? 1 : updateCounts.removeFirst();
                        }
                        case "unwrap" -> { return null; }
                        case "isWrapperFor" -> { return false; }
                        default -> {
                            if (method.getReturnType() == boolean.class) return false;
                            if (method.getReturnType() == int.class) return 0;
                            if (method.getReturnType() == long.class) return 0L;
                            return null;
                        }
                    }
                });
        return holder[0];
    }

    private record FindKey(Class<?> type, Object id) {
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return null;
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
    public <T> EntityGraph<T> createEntityGraph(Class<T> rootType) {
        return null;
    }

    @Override
    public EntityGraph<?> createEntityGraph(String graphName) {
        return null;
    }

    @Override
    public EntityGraph<?> getEntityGraph(String graphName) {
        return null;
    }

    @Override
    public <T> List<EntityGraph<? super T>> getEntityGraphs(Class<T> entityClass) {
        return List.of();
    }
}
