package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@WebTest
public class SpendingWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
            username = "ivan",
            spendings = @Spending(
                    category = "Обучение",
                    description = "Java Advanced 2.0",
                    amount = 90000)
    )
    @Test
    void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
        final String newDescription = "Обучение Niffler Next Generation";
        new LoginPage()
                .open()
                .login("ivan", "123")
                .editSpending(spend.description())
                .editDescription(newDescription)
                .saveChange()
                .checkAlertMessage("Spending is edited successfully")
                .checkThatTableContainsSpending(newDescription);
    }

    @User
    @Test
    void shouldAddNewSpending(UserJson user) {
        String category = "bait";
        int amount = 100;
        LocalDate currentDate = LocalDate.now();
        String description = RandomDataUtils.randomSentence(1);

        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getHeader()
                .addSpendingPage()
                .editCategory(category)
                .editAmount(amount)
                .editDate(currentDate)
                .editDescription(description)
                .saveChange()
                .checkAlertMessage("Spending is edited successfully")
                .getSpendingTable()
                .checkTableContains(description);
    }

}

