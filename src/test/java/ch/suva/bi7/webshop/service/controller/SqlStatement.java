package ch.suva.bi7.webshop.service.controller;

import java.util.Map;

public record SqlStatement(String sql, Map<Object, Object> parameters, boolean nativeQuery) {
}
