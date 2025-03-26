package guru.qa.niffler.test.web;


import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@WebTest
public class FriendsWebTest {

    private static final Config CFG = Config.getInstance();


    @User(
            friends = 1
    )
    @ApiLogin
    @DisplayName("Друг должен присутствовать в таблице друзей")
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        final String friendUsername = user.testData().friendsUsernames()[0];
        Selenide.open(FriendsPage.FRIENDS_PAGE_URL, FriendsPage.class)
                .checkThatFriendsExist(friendUsername);

    }

    @User
    @ApiLogin
    @DisplayName("Таблица друзей должна быть пустой для нового пользователя")
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(FriendsPage.FRIENDS_PAGE_URL, FriendsPage.class)
                .checkThatFriendsDoNotExist();
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @DisplayName("Проверка отображения входящего запроса дружбы в таблице друзей")
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.FRIENDS_PAGE_URL, FriendsPage.class)
                .checkIncomeFriendRequest(user.testData().incomeInvitationsUsernames()[0]);
    }

    @User(outcomeInvitations = 1)
    @DisplayName("Проверка отображения исходящего запроса дружбы в таблице людей")
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        new LoginPage()
                .open()
                .login(user.username(), user.testData().password())
                .getHeader()
                .toAllPeoplesPage()
                .checkOutcomeFriendRequest(user.testData().outcomeInvitationsUsernames()[0]);
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @DisplayName("Проверка возможности принять входящее приглашение дружбы")
    @Test
    void acceptInvitation(UserJson user) {
        final String userIncome = user.testData().incomeInvitationsUsernames()[0];

        Selenide.open(FriendsPage.FRIENDS_PAGE_URL, FriendsPage.class)
                .acceptFriendInvitation(userIncome)
                .checkThatFriendAccepted(userIncome);
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @DisplayName("Проверка возможности отклонить входящее приглашение дружбы")
    @Test
    void declineInvitation(UserJson user) {
        final String userIncome = user.testData().incomeInvitationsUsernames()[0];

        Selenide.open(FriendsPage.FRIENDS_PAGE_URL, FriendsPage.class)
                .declineFriendInvitation(userIncome)
                .checkThatFriendsTableEmpty();
    }
}

