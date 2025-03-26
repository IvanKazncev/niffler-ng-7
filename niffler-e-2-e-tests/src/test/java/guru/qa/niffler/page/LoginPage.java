package guru.qa.niffler.page;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class LoginPage extends BasePage<LoginPage> {


    private final SelenideElement
            usernameInput,
            passwordInput,
            submitButton,
            registerButton,
            errorContainer;

    public LoginPage() {
        this.usernameInput = $("input[name='username']");
        this.passwordInput = $("input[name='password']");
        this.submitButton = $("button[type='submit']");
        this.registerButton = $("a[href='/register']");
        this.errorContainer = $(".form__error");
    }

    public LoginPage(SelenideDriver driver) {
        super(driver);
        this.usernameInput = driver.$("input[name='username']");
        this.passwordInput = driver.$("input[name='password']");
        this.submitButton = driver.$("button[type='submit']");
        this.registerButton = $("a[href='/register']");
        this.errorContainer = $(".form__error");
    }

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

    public void login(String username, String password,SelenideDriver driver) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        submitButton.click();
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

    @Step("Открытие страницы авторизации")
    @Nonnull
    public LoginPage open(@NotNull SelenideDriver driver) {
        driver.open(LOGIN_PAGE_URL);
        return this;
    }

    @Step("Проверка, что страница загружена")
    @Nonnull
    public LoginPage checkThatPageLoaded() {
        usernameInput.should(visible);
        passwordInput.should(visible);
        return this;
    }
}
