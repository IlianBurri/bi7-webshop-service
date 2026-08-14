package ch.suva.bi7.webshop.service.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBConnection {

    /**
     * Führt ein SQL-Statement ohne Parameter aus (delegiert an die
     * parameterisierte Variante).
     */
    default ResultSet execute(String sql) throws SQLException {
        return execute(sql, new Object[0]);
    }

    /**
     * Führt ein UPDATE/INSERT/DELETE ohne Parameter aus (delegiert an die
     * parameterisierte Variante).
     */
    default int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql, new Object[0]);
    }

    /**
     * Führt ein SQL-Statement mit Platzhaltern (?) aus. Die Werte werden als
     * PreparedStatement-Parameter gebunden und können so nicht per SQL-Injection
     * in die Query eingeschleust werden.
     */
    ResultSet execute(String sql, Object... params) throws SQLException;

    /**
     * Führt ein UPDATE/INSERT/DELETE mit Platzhaltern (?) aus. Die Werte werden als
     * PreparedStatement-Parameter gebunden und können so nicht per SQL-Injection
     * in die Query eingeschleust werden.
     */
    int executeUpdate(String sql, Object... params) throws SQLException;

    /**
     * Führt ein INSERT mit Platzhaltern (?) aus und liefert den automatisch
     * generierten Primärschlüssel (AUTO_INCREMENT) direkt aus JDBC zurück.
     * So muss die ID nicht über einen nachgelagerten SELECT wieder gesucht
     * werden (vermeidet Race Conditions bei Parallelität).
     *
     * Default: wird nur von {@link DBConnectionImpl} unterstützt; alle anderen
     * Implementierungen (z. B. Test-Mocks) können die Methode gezielt überschreiben.
     */
    default int executeUpdateReturningGeneratedKeys(String sql, Object... params) throws SQLException {
        throw new UnsupportedOperationException(
                "executeUpdateReturningGeneratedKeys wird von dieser Implementierung nicht unterstützt");
    }

    void close();
}
