package ch.suva.bi7.webshop.service.db;

import java.sql.*;

public class DBConnectionImpl implements DBConnection {

    private final Connection con;

    public DBConnectionImpl(String host, String schema, String user, String password) throws ClassNotFoundException, InstantiationException, IllegalAccessException, SQLException {
        // JDBC-Treiber registrieren. Das ist ggf. nicht notwendig, kann also auch ohne diese Zeile funktionieren:
        Class.forName("org.mariadb.jdbc.Driver").newInstance();

        con = DriverManager.getConnection("jdbc:mariadb://" + host + "/" + schema + "?" +
                "user=" + user + "&password=" + password + "&useSSL=false");
        System.out.println("DB-Connection: " + con);
    }

    @Override
    public ResultSet execute(String sql) throws SQLException {
        Statement s = con.createStatement();
        if (s.execute(sql)) {
            return s.getResultSet();
        }
        return null;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        Statement s = con.createStatement();
        return s.executeUpdate(sql);
    }

    @Override
    public void close() {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {}
        }
    }
}
