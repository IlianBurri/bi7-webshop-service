package ch.suva.bi7.webshop.service.mock;

import ch.suva.bi7.webshop.service.dao.UserDao;
import ch.suva.bi7.webshop.service.db.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EinfachesUserDaoMock implements UserDao {
    public UserEntity gespeicherterUser = null;
    private Optional<UserEntity> vorgegebenerUser;

    public EinfachesUserDaoMock(Optional<UserEntity> vorgegebenerUser) {
        this.vorgegebenerUser = vorgegebenerUser;
    }

    @Override
    public void addUser(UserEntity user) throws Exception {
        this.gespeicherterUser = user;
    }

    @Override
    public Optional<UserEntity> getUserByEMail(String email) {
        return vorgegebenerUser;
    }

    @Override
    public List<String> getAllUsernames() {
        return new ArrayList<>();
    }
}
