package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.SearchField;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@Getter
@ParametersAreNonnullByDefault
public class PeoplePage extends BasePage<MainPage> {

    private final SearchField searchField = new SearchField();
    private final Header header = new Header();

    private final SelenideElement
            allPeopleTable = $("#all"),
            pagePrevButton = $("#page-prev"),
            pageNextButton = $("#page-next"),
            peopleTab = $("a[href='/people/friends']"),
            allTab = $("a[href='/people/all']");

    public void checkOutcomeFriendRequest(String outcomeUserName) {
        searchField.search(outcomeUserName);
        allPeopleTable.$$("tr").findBy(text(outcomeUserName))
                .shouldHave(text("Waiting..."));

    }


    @Step("Проверка, что страница загружена")
    @Nonnull
    public PeoplePage checkThatPageLoaded() {
        peopleTab.shouldBe(Condition.visible);
        allTab.shouldBe(Condition.visible);
        return this;
    }
}
