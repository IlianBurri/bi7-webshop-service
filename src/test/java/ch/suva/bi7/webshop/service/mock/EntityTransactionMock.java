package ch.suva.bi7.webshop.service.mock;

import jakarta.persistence.EntityTransaction;

import java.util.List;

public class EntityTransactionMock implements EntityTransaction {

    private List<String> actions;

    public EntityTransactionMock(List<String> actions) {
        this.actions = actions;
    }

    @Override
    public void begin() {
        actions.add("EntityTransaction.begin");
    }

    @Override
    public void commit() {
        actions.add("EntityTransaction.commit");
    }

    @Override
    public void rollback() {

    }

    @Override
    public void setRollbackOnly() {

    }

    @Override
    public boolean getRollbackOnly() {
        return false;
    }

    @Override
    public boolean isActive() {
        return false;
    }
}
