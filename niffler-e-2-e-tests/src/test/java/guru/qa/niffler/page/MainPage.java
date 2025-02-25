package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.NavigateMenuComponent;
import guru.qa.niffler.page.components.SearchField;
import guru.qa.niffler.page.components.SpendingTable;
import io.qameta.allure.Step;
import lombok.Getter;
import org.openqa.selenium.By;

import javax.annotation.Nonnull;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

@Getter
public class MainPage extends BasePage<MainPage> {


    private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
    private final SelenideElement historyOfSpending = $("#spendings");
    private final SelenideElement statistic = $("#stat");
    private final SelenideElement newSpendingLink = $(By.xpath("//a[@href = '/spending']"));
    private final SelenideElement menuBtn = $("button[aria-label='Menu']");
    private final SelenideElement friendsLink = $("a[href='/people/friends']");
    private final SelenideElement allPeopleLink = $("a[href='/people/all']");
    private final SelenideElement pieChart = $("canvas[role='img']");
    private final ElementsCollection categoryContainerComponents = $$("#legend-container li");

    public final NavigateMenuComponent navigateMenuComponent = new NavigateMenuComponent();
    private final SearchField searchField = new SearchField();
    private final Header header = new Header();
    private final SpendingTable spendingTable = new SpendingTable();

    public EditSpendingPage editSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public EditSpendingPage addNewSpending() {
        newSpendingLink.click();
        return new EditSpendingPage();
    }

    public FriendsPage openFriendsPage() {
        menuBtn.click();
        friendsLink.should(visible).click();
        return new FriendsPage();
    }

    public AllInvPage openAllPeoplePage() {
        menuBtn.click();
        allPeopleLink.should(visible).click();
        return new AllInvPage();
    }

    public void checkThatTableContainsSpending(String spendingDescription) {
        tableRows.find(text(spendingDescription)).should(visible);
    }

    public void checkThatMainPageVisible() {
        historyOfSpending.should(visible);
        statistic.should(visible);
    }

    @Step("Проверяем в блоке Statistics ячейку с категорией и суммой")
    public void checkCellCategoryAndAmountInStatisticsBlock(String categoryName, String amount) {
        categoryContainerComponents.findBy(text(categoryName)).shouldHave(text(amount));
    }
    @Step("Нажатие кнопки изменения траты {spendingDescription}")
    @Nonnull
    public EditSpendingPage editSpendingClick(String spendingDescription) {
        searchField.search(spendingDescription);
        tableRows.find(text(spendingDescription)).$("td", 5).click();

        return new EditSpendingPage();
    }

}
