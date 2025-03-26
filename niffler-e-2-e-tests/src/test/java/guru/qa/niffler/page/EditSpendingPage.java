package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Calendar;
import guru.qa.niffler.page.components.Header;
import io.qameta.allure.Step;
import lombok.Getter;


import javax.annotation.Nonnull;

import java.time.LocalDate;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditSpendingPage extends BasePage<EditSpendingPage> {

    public static final String EDIT_SPEND_PAGE_URL = CONFIG.frontUrl() + "spending";

    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement saveBtn = $("#save");
    private final SelenideElement
            saveButton = $("#save"),
            amountInput = $("#amount"),
            categoryInput = $("#category"),
            cancelButton = $("#cancel"),
            alert = $(".MuiSnackbar-root");

    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    public void save() {
        saveBtn.click();
    }

    private final Header header = new Header();
    private final Calendar calendar = new Calendar();


    @Step("Изменение названия траты {description}")
    @Nonnull
    public EditSpendingPage editDescription(String description) {
        descriptionInput.clear();
        descriptionInput.setValue(description);
        return this;
    }

    @Step("Изменение категории траты {category}")
    @Nonnull
    public EditSpendingPage editCategory(String category) {
        categoryInput.clear();
        categoryInput.setValue(category);
        return this;
    }

    @Step("Изменение даты для траты {date}")
    @Nonnull
    public EditSpendingPage editDate(LocalDate date) {
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Step("Изменение суммы траты {amount}")
    @Nonnull
    public EditSpendingPage editAmount(double amount) {
        amountInput.clear();
        amountInput.setValue(String.valueOf(amount));
        return this;
    }

    @Step("Сохранение изменений")
    @Nonnull
    public MainPage saveChange() {
        saveButton.click();
        return new MainPage();
    }
    @Step("Проверка, что страница загружена")
    @Nonnull
    public EditSpendingPage checkThatPageLoaded() {
        amountInput.should(visible);
        return this;
    }
}
