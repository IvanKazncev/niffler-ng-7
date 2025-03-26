package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.helpers.api.RestClient;
import guru.qa.niffler.helpers.api.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CONFIG.authUrl(), true);
        this.authApi = create(AuthApi.class);
    }

    @Step("Авторизация с хешем codeChallenge = {codeChallenge} через API")
    public void preRequest(String codeChallenge) {

        final Response response;
        try {
            response = authApi.authorize(
                    "code",
                    "client",
                    "openid",
                    CONFIG.frontUrl() + "authorized",
                    codeChallenge,
                    "S256"
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Step("Запрос на вход в систему с помощью {username}/{password}")
    public void login(String username, String password) {
        final String code;
        try {
            code = authApi.login(
                            username,
                            password,
                            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                    ).execute()
                    .raw()
                    .request()
                    .url()
                    .queryParameter("code");
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        TestMethodContextExtension.context()
                .getStore(ExtensionContext.Namespace.create(AuthApiClient.class))
                .put(
                        "code",
                        code
                );
    }
    @Nonnull
    @Step("Получение авторизационного токена")
    public String token(String code, String codeVerifier) {
        final Response<JsonNode> response;

        try {
            response = authApi.token(
                    "client",
                    CONFIG.frontUrl() + "authorized",
                    "authorization_code",
                    code,
                    codeVerifier

            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return Objects.requireNonNull(response.body()).get("id_token").asText();
    }
}
