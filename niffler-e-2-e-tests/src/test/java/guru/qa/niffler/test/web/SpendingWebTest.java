package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Bubble;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
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
    @ApiLogin
    @Test
    void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
        final String newDescription = "Обучение Niffler Next Generation";
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .checkAlertMessage("Spending is edited successfully")
                .checkThatTableContainsSpending(newDescription);
    }

    @User
    @ApiLogin
    @Test
    void shouldAddNewSpending(UserJson user) {
        String category = "bait";
        int amount = 100;
        LocalDate currentDate = LocalDate.now();
        String description = RandomDataUtils.randomSentence(1);

        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
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
    @ApiLogin
    @ScreenShotTest(value = "image/expected-edit-stat.png")
    void checkStatComponentAfterEditSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        String newAmount = "2000";
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
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
    @ApiLogin
    @ScreenShotTest(value = "image/expected-delete-stat.png",
            rewrite = true)
    void checkStatComponentAfterDeleteSpendingTest(@NotNull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
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
    @ApiLogin
    @ScreenShotTest(value = "image/expected-archived-stat.png")
    void checkStatComponentAfterArchivedCategoryTest(@NotNull UserJson user, BufferedImage expected) throws IOException, InterruptedException {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getStatComponent()
                .checkCellCategoryAndAmountInStatisticsBlock("Archived", String.format("%.0f", user.testData().spends().getFirst().amount()))
                .checkStatisticImage(expected)
                .checkBubbles(new Bubble(Color.yellow,"stady 2000 ₽"));

    }
    @DisplayName("Проверяем, что состояние диаграммы трат имеет Bubbles в любом порядке")
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(category = "Pet a duck",
                            description = "Hobbies",
                            amount = 2000
                    ),
                    @Spending(category = "Сосиска",
                            description = "Съесть сосиску",
                            amount = 5000
                    )
            }
    )
    @ApiLogin
    @Test
    void checkBubblesInAnyOderTest(UserJson user) {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getStatComponent()
                .checkBubblesInAnyOrder(
                        new Bubble(Color.yellow, "Обучение 79990 ₽"),
                        new Bubble(Color.green, "Сосиска 5000 ₽"),
                        new Bubble(Color.orange, "Pet a duck 2000 ₽")
                );
    }

    @DisplayName("Проверка содержания в состоянии диаграммы цветов и текстов для искомых трат")
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(category = "Pet a duck",
                            description = "Hobbies",
                            amount = 2000
                    ),
                    @Spending(category = "Сосиска",
                            description = "Съесть сосиску",
                            amount = 5000
                    )
            }
    )
    @ApiLogin
    @Test
    void checkStatComponentContainsBubblesTest(UserJson user) {
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getStatComponent()
                .checkBubblesContains(
                        new Bubble(Color.yellow, "Обучение 79990 ₽"),
                        new Bubble(Color.orange, "Pet a duck 2000 ₽")
                );
    }

    @DisplayName("Проверка наличия в таблице статистики трат искомых")
    @User(
            spendings = {
                    @Spending(
                            category = "Обучение",
                            description = "Обучение Advanced 2.0",
                            amount = 79990
                    ),
                    @Spending(category = "Pet a duck",
                            description = "Hobbies",
                            amount = 2000
                    )
            }

    )
    @ApiLogin
    @Test
    void checkSpendExistTest(UserJson user) {

        SpendJson[] expectedSpends = user.testData().spends().toArray(SpendJson[]::new);
        Selenide.open(MainPage.MAIN_PAGE_URL, MainPage.class)
                .getSpendingTable()
                .checkSpendingTable(expectedSpends);
    }
}

