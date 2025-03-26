package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.pageble.RestResponsePage;
import guru.qa.niffler.service.GatewayV2ApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@RestTest
public class FriendsV2RestTest {

    @RegisterExtension
    private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.restApiLoginExtension();

    private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();

    @ApiLogin
    @User(friends = 1, incomeInvitations = 1)
    @Test
    void friendsAndIncomeInvitationsListShouldBeReturned(UserJson user, @Token String token) {
        final UserJson expectedFriend = user.testData().friends().getFirst();
        final UserJson expectedInvitation = user.testData().incomeInvitations().getFirst();

        final RestResponsePage<UserJson> response = gatewayApiClient.allFriends(token, 0, 2, null, null);

        Assertions.assertEquals(2, response.getContent().size());

        final UserJson actualInvitation = response.getContent().getFirst();
        final UserJson actualFriend = response.getContent().getLast();

        Assertions.assertEquals(expectedFriend.id(), actualFriend.id());
        Assertions.assertEquals(expectedInvitation.id(), actualInvitation.id());
    }
}