package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class LoginTest {

  private static final Config CFG = Config.getInstance();

  @DisplayName("Отображение главной страницы после успешной авторизации")
  @User(
          categories = {
                  @Category(name = "Магазины", archived = false),
                  @Category(name = "Бары", archived = true)
          },
          spendings = {
                  @Spending(
                          category = "Обучение",
                          description = "QA.GURU Advanced 7",
                          amount = 80000
                  )
          }
  )
  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login(user.username(), user.testData().password());
    $(".css-giaux5").shouldBe(visible)
            .shouldHave(text("Statistics"));
    $(".css-uxhuts").shouldBe(visible);
  }


  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .login("ivan", "123");
    $(".header").shouldBe(visible)
            .shouldHave(text("Log in"));
    $(".form__error").shouldBe(visible)
            .shouldHave(text("Неверные учетные данные пользователя"));
  }
}
