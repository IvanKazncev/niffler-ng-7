package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class LoginPage extends BasePage<LoginPage> {


    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement registerButton = $("a[href='/register']");
    private final SelenideElement errorContainer = $(".form__error");
    private final static String LOGIN_PAGE_URL = Config.getInstance().authUrl() + "login";

    public RegisterPage doRegister() {
        registerButton.click();
        return new RegisterPage();
    }

    public MainPage successLogin(String username, String password) {
        login(username, password);
        return new MainPage();
    }

    public MainPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
        return new MainPage();
    }

    public LoginPage checkError(String error) {
        errorContainer.shouldHave(text(error));
        return this;
    }

    @Step("Открытие страницы авторизации")
    @Nonnull
    public LoginPage open() {
        Selenide.open(LOGIN_PAGE_URL);
        return this;
    }
}
