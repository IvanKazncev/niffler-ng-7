package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public abstract class BasePage<T extends BasePage<?>> {

    protected static final Config CONFIG = Config.getInstance();

    private final SelenideElement alert = $(".MuiSnackbar-root");

    @Step("Проверка, что alert-message содержит текст {value}")
    @Nonnull
    public T checkAlertMessage(String value) {
        alert.should(visible).should(text(value));
        return (T) this;
    }
}
