package ch.suva.bi7.webshop.service.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnectionImpl implements DBConnection {

    private static final Logger logger = LoggerFactory.getLogger(DBConnectionImpl.class);

    private final Connection con;

    public DBConnectionImpl(String host, String schema, String user, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver").newInstance();

        con = DriverManager.getConnection("jdbc:mariadb://" + host + "/" + schema + "?" +
                "user=" + user + "&password=" + password + "&useSSL=false");
        logger.info("DB-Connection hergestellt: {}", con);
    }

    @Override
    public ResultSet execute(String sql, Object... params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        bindParams(ps, params);
        if (ps.execute()) {
            return ps.getResultSet();
        }
        return null;
    }

    @Override
    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql);
        bindParams(ps, params);
        return ps.executeUpdate();
    }

    @Override
    public int executeUpdateReturningGeneratedKeys(String sql, Object... params) throws SQLException {
        PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        bindParams(ps, params);
        ps.executeUpdate();
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new SQLException("Kein generierter Schlüssel wurde zurückgegeben");
        }
    }

    @Override
    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                logger.warn("Fehler beim Schliessen der DB-Connection", e);
            }
        }
    }

    private void bindParams(PreparedStatement ps, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
