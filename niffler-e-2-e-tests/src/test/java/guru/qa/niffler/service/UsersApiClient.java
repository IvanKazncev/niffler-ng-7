package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.api.RestClient;
import guru.qa.niffler.helpers.api.ThreadSafeCookieStore;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;
import org.apache.hc.core5.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient{

    private static final Config CONFIG = Config.getInstance();

    private static final String PASSWORD = "12345";

    private final AuthApi authApi = new RestClient.EmtyRestClient(CONFIG.authUrl()).create(AuthApi.class);
    private final UserdataApi userdataApi = new RestClient.EmtyRestClient(CONFIG.userdataUrl()).create(UserdataApi.class);

    @NotNull
    @Override
    @Step("Создание пользователя через API")
    public UserJson createUser(String username, String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
            UserJson createdUser = requireNonNull(userdataApi.currentUser(username).execute().body());
            return createdUser.addTestData(
                    new TestData(
                            password
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    @Step("Добавление входящего приглашения дружбы через API")
    public void addIncomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, PASSWORD);
                    response = userdataApi.sendInvitation(
                            newUser.username(),
                            targetUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new AssertionError(e);
                }
                assertEquals(200, response.code());
                targetUser.testData()
                        .incomeInvitations()
                        .add(newUser);
            }
        }
    }
    @Override
    @Step("Добавление исходящего приглашения дружбы через API")
    public void addOutcomeInvitation(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                final UserJson newUser;
                try {
                    newUser = createUser(username, PASSWORD);
                    response = userdataApi.sendInvitation(
                            targetUser.username(),
                            newUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
                targetUser.testData()
                        .outcomeInvitations()
                        .add(newUser);
            }
        }
    }
    @Override
    @Step("Добавление действующей дружбы через API")
    public void addFriend(UserJson targetUser, int count) {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final String username = randomUsername();
                final Response<UserJson> response;
                try {
                    userdataApi.sendInvitation(
                            createUser(
                                    username,
                                    PASSWORD
                            ).username(),
                            targetUser.username()
                    ).execute();
                    response = userdataApi.acceptInvitation(targetUser.username(), username).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, response.code());
                targetUser.testData()
                        .friends()
                        .add(response.body());
            }
        }
    }

    @Step("Получить всех юзеров")
    public @Nonnull List<UserJson> getAllUsers(String username) {
        final Response<List<UserJson>> response;
        try {
            response = userdataApi.allUsers(username, null)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(HttpStatus.SC_SUCCESS, response.code());
        return response.body() != null ? response.body() : Collections.emptyList();
    }
    @Step("Get friends of a user '{username}'")
    @Nonnull
    public List<UserJson> getFriends(String username) {
        final Response<List<UserJson>> response;

        try {
            response = userdataApi.friends(
                    username,
                    null
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(200, response.code());

        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            return Collections.emptyList();
        }
    }
    @Step("Get 'All people' list of a user '{username}'")
    @Nonnull
    public List<UserJson> getAllPeople(String username) {
        final Response<List<UserJson>> response;

        try {
            response = userdataApi.allUsers(
                    username,
                    null
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertEquals(200, response.code());

        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            return Collections.emptyList();
        }
    }
}
