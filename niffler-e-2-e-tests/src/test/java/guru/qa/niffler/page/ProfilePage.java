package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.NavigateMenuComponent;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class ProfilePage extends BasePage<MainPage> {

    private final SelenideElement avatar = $("#image__input").parent().$("img");
    private final SelenideElement userName = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement photoInput = $("input[type='file']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement categoryInput = $("input[name='category']");
    private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
    private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
    private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");
    private final SelenideElement showArchivedCheckbox = $("input[type='checkbox']");


    NavigateMenuComponent navigateMenuComponent = new NavigateMenuComponent();

    private final Header header = new Header();

    private final SelenideElement
            uploadNewPictureButton = $("input#image__input"),
            saveChangesButton = $("button[type='submit']"),
            alert = $(".MuiSnackbar-root");

    private final ElementsCollection
            listCategory = $("div").$$("div[role='button']"),
            archiveSubmitButtons = $$("div[role='dialog'] button");

    @Step("Архивирование категории {categoryName}")
    public ProfilePage archivedCategory(String categoryName) {
        listCategory.find(text(categoryName)).parent().parent().$("button[aria-label='Archive category']").click();
        archiveSubmitButtons.find(text("Archive")).click();
        return this;
    }

    @Nonnull
    @Step("Загрузка изображения {file} на странице профиля")
    public ProfilePage uploadImage(String file) {
        uploadNewPictureButton.uploadFromClasspath(file);
        return this;
    }

    @Nonnull
    @Step("Ввод имени пользователя {name} на странице профиля")
    public ProfilePage setName(String name) {
        nameInput.setValue(name);
        return this;
    }

    @Nonnull
    @Step("Ввод новой категории {newCategory} на странице профиля")
    public ProfilePage setNewCategory(String newCategory) {
        categoryInput.setValue(newCategory);
        return this;
    }

    @Nonnull
    @Step("Нажатие на чек-бокс отображения архивных категорий на странице профиля")
    public ProfilePage clickArchivedCheckbox() {
        showArchivedCheckbox.click();
        return this;
    }


    @Step("Проверка нахождения категории {categoryName} в отображаемом списке на странице профиля")
    public void checkCategoryInCategoryList(String categoryName) {
        $$(".css-17u3xlq").findBy(text(categoryName)).shouldBe(visible);
    }

    @Nonnull
    @Step("Сохранение изменений на странице пользователя")
    public ProfilePage saveChanges() {
        saveChangesButton.click();
        return this;
    }
}
