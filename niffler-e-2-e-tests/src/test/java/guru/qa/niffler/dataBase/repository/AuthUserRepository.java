package guru.qa.niffler.dataBase.repository;

import guru.qa.niffler.dataBase.entity.AuthUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {
    AuthUserEntity createUser(AuthUserEntity authUser);
    Optional<AuthUserEntity> findById(UUID id);
    List<AuthUserEntity> findAll();
}
