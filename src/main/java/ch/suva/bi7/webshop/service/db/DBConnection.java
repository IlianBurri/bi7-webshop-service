package ch.suva.bi7.webshop.service.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBConnection {

    default ResultSet execute(String sql) throws SQLException {
        return execute(sql, new Object[0]);
    }

    default int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql, new Object[0]);
    }

    ResultSet execute(String sql, Object... params) throws SQLException;

    int executeUpdate(String sql, Object... params) throws SQLException;

    default int executeUpdateReturningGeneratedKeys(String sql, Object... params) throws SQLException {
        throw new UnsupportedOperationException(
                "executeUpdateReturningGeneratedKeys wird von dieser Implementierung nicht unterstützt");
    }

    void close();
}
