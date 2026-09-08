package ch.suva.bi7.webshop.service.dao;

import ch.suva.bi7.webshop.service.db.entity.UserEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<UserEntity> getUserByEMail(String email) throws SQLException;

    List<String> getAllUsernames() throws SQLException;

    void addUser(UserEntity newUser) throws Exception;
}
