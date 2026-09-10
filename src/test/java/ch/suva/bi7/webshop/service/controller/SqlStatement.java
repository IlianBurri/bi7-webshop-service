package ch.suva.bi7.webshop.service.controller;

import java.util.List;

record SqlStatement(String sql, List<Object> params) {
}
