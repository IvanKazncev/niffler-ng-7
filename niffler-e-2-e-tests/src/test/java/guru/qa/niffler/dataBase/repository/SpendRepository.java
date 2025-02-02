package guru.qa.niffler.dataBase.repository;

import guru.qa.niffler.dataBase.entity.SpendEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {
    SpendEntity create(SpendEntity spendEntity);

    Optional<SpendEntity> findSpendById(UUID id);

    List<SpendEntity> findAllByUsername(String username);

    void deleteSpend(SpendEntity spendEntity);

    List<SpendEntity> findAll();
}
