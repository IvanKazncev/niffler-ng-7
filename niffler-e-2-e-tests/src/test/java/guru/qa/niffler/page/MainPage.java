package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.NavigateMenuComponent;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

  private final ElementsCollection tableRows = $("#spendings tbody").$$("tr");
  private final SelenideElement historyOfSpending = $("#spendings");
  private final SelenideElement statistic = $("#stat");
  private final SelenideElement newSpendingLink = $(By.xpath("//a[@href = '/spending']"));
  private final SelenideElement menuBtn = $("button[aria-label='Menu']");
  private final SelenideElement friendsLink = $("a[href='/people/friends']");
  private final SelenideElement allPeopleLink = $("a[href='/people/all']");

  public final NavigateMenuComponent navigateMenuComponent = new NavigateMenuComponent();

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
}
