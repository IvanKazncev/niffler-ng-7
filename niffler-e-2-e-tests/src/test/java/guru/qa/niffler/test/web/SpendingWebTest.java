package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.DisabledByIssue;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;

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
    @DisplayName("Проверка компонента статистики после редактирования траты")
    @User(
            spendings = @Spending(
                    category = "bait",
                    description = "stady",
                    amount = 1000
            )
    )
    @ScreenShotTest(value = "image/expected-edit-stat.png")
    void checkStatComponentAfterEditSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        String newAmount = "2000";
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .editSpendingClick(user.testData().spends().getFirst().description())
                .editAmount(Double.parseDouble(newAmount))
                .saveChange()
                .checkCellCategoryAndAmountInStatisticsBlock(user.testData().spends().getFirst().category().name(),
                        newAmount);

        Thread.sleep(2000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(new MainPage().getPieChart().screenshot()));

        Assertions.assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }

    @DisplayName("Проверка компонента статистики после удаления траты")
    @User(
            spendings = {
                    @Spending(
                            category = "bait2",
                            description = "wolf",
                            amount = 1000),
                    @Spending(
                            category = "bait",
                            description = "stady",
                            amount = 2000)
            }
    )

    @ScreenShotTest(value = "image/expected-delete-stat.png",
            rewrite = true)
    void checkStatComponentAfterDeleteSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getSpendingTable()
                .deleteSpending(user.testData().spends().getFirst().category().name());

        Thread.sleep(2000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(new MainPage().getPieChart().screenshot()));

        Assertions.assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }

    @DisplayName("Проверка компонента статистики после редактирования траты")
    @User(
            spendings = @Spending(
                    category = "bait",
                    description = "stady",
                    amount = 2000
            )
    )
    @ScreenShotTest(value = "image/expected-archived-stat.png")
    void checkStatComponentAfterArchivedCategoryTest(@NotNull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getHeader()
                .toProfilePage()
                .archivedCategory(user.testData().spends().getFirst().category().name())
                .getHeader()
                .toMainPage()
                .checkCellCategoryAndAmountInStatisticsBlock("Archived",String.format("%.0f",user.testData().spends().getFirst().amount()));

        Thread.sleep(2000);
        BufferedImage actual = ImageIO.read(Objects.requireNonNull(new MainPage().getPieChart().screenshot()));

        Assertions.assertFalse(new ScreenDiffResult(
                actual,
                expected
        ));
    }

}

