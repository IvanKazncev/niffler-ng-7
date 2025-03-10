package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@WebTest
public class ProfileTest {

  private static final Config CFG = Config.getInstance();

  @User(
          username = "ivan",
          categories = @Category( archived = true)
  )
  @DisplayName("Архивная категория должна присутствовать и отображаться в списке категорий")
  @Test
  void archivedCategoryShouldPresentInCategoriesList(@NotNull CategoryJson[] category) {
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
  void activeCategoryShouldPresentInCategoriesList(@NotNull CategoryJson category) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login("ivan", "123")
            .navigateMenuComponent
            .clickAccountMenuButton()
            .clickProfileButton()
            .checkCategoryInCategoryList(category.name());
  }
  @User
  @Test
  void updateAllFieldsProfile(@NotNull UserJson user) {
    new LoginPage()
            .open()
            .login(user.username(), user.testData().password())
            .getHeader()
            .toProfilePage()
            .uploadImage("resources/img/bait.png")
            .setName(RandomDataUtils.randomName())
            .setNewCategory(RandomDataUtils.randomCategoryName())
            .saveChanges()
            .checkAlertMessage("Profile successfully updated");
  }

  @User
  @DisplayName("Проверка загрузки аватарки")
  @ScreenShotTest(value = "image/expected-avatar.png")
  void checkCorrectUploadAvatar(@NotNull UserJson user, BufferedImage expected) throws IOException {
    new LoginPage()
            .open()
            .login(user.username(), user.testData().password())
            .getHeader()
            .toProfilePage()
            .uploadImage("resources/img/bait.png")
            .saveChanges();

    BufferedImage actual = ImageIO.read(Objects.requireNonNull(new ProfilePage().getAvatar().screenshot()));
    Assertions.assertFalse(new ScreenDiffResult(
            actual,
            expected
    ));
  }
}
