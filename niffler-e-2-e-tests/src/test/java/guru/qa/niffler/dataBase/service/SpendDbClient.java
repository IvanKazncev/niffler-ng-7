package guru.qa.niffler.dataBase.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.dataBase.dbConnection.DataBases;
import guru.qa.niffler.dataBase.entity.CategoryEntity;
import guru.qa.niffler.dataBase.entity.SpendEntity;
import guru.qa.niffler.dataBase.impl.CategoryDaoJdbc;
import guru.qa.niffler.dataBase.impl.SpendDaoJdbc;
import guru.qa.niffler.dataBase.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendingJson;

import java.util.List;
import java.util.Optional;

public class SpendDbClient {


    private static final Config CFG = Config.getInstance();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJDBCUrl()
    );
    public SpendingJson createSpend(SpendingJson spendJson) throws Exception {
        return jdbcTxTemplate.transaction(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendJson);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = new CategoryDaoJdbc().create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendingJson.fromEntity(
                  new SpendDaoJdbc().create(spendEntity)
            );
        });
    }

    public CategoryJson createCategory(CategoryJson categoryJson) throws Exception {
        return jdbcTxTemplate.transaction(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            return CategoryJson.fromEntity(
                    new CategoryDaoJdbc().create(categoryEntity)
            );
        });
    }

    public void deleteSpend(SpendingJson spendingJson) throws Exception {
        jdbcTxTemplate.transaction(() -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spendingJson);
            new SpendDaoJdbc().deleteSpend(spendEntity);
            return null;
        });
    }

    public void deleteCategory(CategoryJson categoryJson) throws Exception {
        jdbcTxTemplate.transaction(() -> {
            CategoryEntity categoryEntity = CategoryEntity.fromJson(categoryJson);
            new CategoryDaoJdbc().deleteCategory(categoryEntity);
            return null;
        });
    }

    public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String categoryName) throws Exception {
        return jdbcTxTemplate.transaction(() -> {
            return new CategoryDaoJdbc().findCategoryByUsernameAndCategoryName(username,categoryName)
                    .map(CategoryJson::fromEntity);
        });
    }

    public List<SpendEntity> findAllSpendsByUserName(String userName) throws Exception {
        return jdbcTxTemplate.transaction(() -> {
            return new SpendDaoJdbc().findAllByUsername(userName);
        });
    }
}
