package guru.qa.niffler.test.web;

import guru.qa.niffler.api.AuthApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.OAuthUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;


import static guru.qa.niffler.utils.OAuthUtils.*;
import static guru.qa.niffler.utils.OAuthUtils.generateCodeVerifier;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AuthTest {
    private final AuthApiClient authApi = new AuthApiClient();

    @User
    @Test
    public void oAuthTest(UserJson user) {
        final String codeVerifier = generateCodeVerifier();
        final String codeChallenge = generateCodeChallange(codeVerifier);

        authApi.login(user.username(), user.testData().password());
        String code = (String) TestMethodContextExtension
                .context()
                .getStore(ExtensionContext.Namespace.create(AuthApiClient.class))
                .get("code");

    }
}
