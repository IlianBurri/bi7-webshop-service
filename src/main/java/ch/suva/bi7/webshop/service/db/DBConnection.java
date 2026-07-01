package ch.suva.bi7.webshop.service.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBConnection {
    ResultSet execute(String sql) throws SQLException;

    int executeUpdate(String sql) throws SQLException;

    void close();
}
