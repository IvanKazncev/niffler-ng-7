package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.api.RestClient;
import guru.qa.niffler.helpers.api.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.utils.OAuthUtils;
import io.qameta.allure.Step;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthApiClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    public String login(String username, String password) {
        final String codeVerifier = OAuthUtils.generateCodeVerifier();
        final String codeChallenge = OAuthUtils.generateCodeChallange(codeVerifier);
        final String redirectUri = CFG.frontUrl() + "authorized";
        String clientId = "client";

        authApi.authorize(
                "code",
                clientId,
                "openid",
                redirectUri,
                codeChallenge,
                "S256"

        ).execute();

        authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        Response<JsonNode> tokenResponse = authApi.token(
                clientId,
                redirectUri,
                "authorization_code",
                ApiLoginExtension.getCode(),
                codeVerifier
        ).execute();

        return tokenResponse.body().get("id_token").asText();
    }
}
