package guru.qa.niffler.dataBase.dao;

import guru.qa.niffler.dataBase.entity.CategoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {

    CategoryEntity create(CategoryEntity category);

    void update(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String categoryName);

    List<CategoryEntity> findAllByUsername(String username);

    void deleteCategory(CategoryEntity category);
}
