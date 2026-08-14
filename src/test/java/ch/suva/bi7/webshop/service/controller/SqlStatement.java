package ch.suva.bi7.webshop.service.controller;

import java.util.List;

/**
 * Test-Hilfsklasse: hält ein ausgeführtes SQL-Statement samt der gebundenen
 * PreparedStatement-Parameter. Damit kann geprüft werden, dass Werte als
 * Parameter (?) und nicht per String-Konkatenation in die Query gelangen.
 */
record SqlStatement(String sql, List<Object> params) {
}
