package guru.qa.niffler.dataBase.repository;

import guru.qa.niffler.dataBase.entity.UserEntity;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UseDataRepository {

    UserEntity createUser(UserEntity user);

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    void delete(UserEntity user);

    List<UserEntity> findAll() throws SQLException;
}
