package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

  private static final Config CFG = Config.getInstance();

  @User(
          username = "ivan",
          categories = @Category( archived = true)
  )
  @DisplayName("Архивная категория должна присутствовать и отображаться в списке категорий")
  @Test
  void archivedCategoryShouldPresentInCategoriesList(CategoryJson[] category) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(category[0].username(), "123")
            .navigateMenuComponent
            .clickAccountMenuButton()
            .clickProfileButton()
            .clickArchivedCheckbox()
            .checkCategoryInCategoryList(category[0].name());
  }

  @User(
          username = "ivan",
          categories = @Category( archived = false)
  )
  @DisplayName("Активная категория должна присутствовать и отображаться в списке категорий")
  @Test
  void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login("ivan", "123")
            .navigateMenuComponent
            .clickAccountMenuButton()
            .clickProfileButton()
            .checkCategoryInCategoryList(category.name());
  }
}
