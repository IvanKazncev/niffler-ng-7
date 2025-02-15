package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.helpers.api.RestClient;
import lombok.SneakyThrows;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class GhApiClient extends RestClient {

    private static final String GH_TOKEN_ENV = "GITHUB_TOKEN";

    private final GhApi ghApi;

    public GhApiClient() {
        super(CONFIG.ghUrl());
        this.ghApi = create(GhApi.class);
    }

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Config.getInstance().ghUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    @Nonnull
    public String issueState(String issueNumber) throws IOException {
        JsonNode responseBody = ghApi.issue(
                "Bearer " + System.getenv(GH_TOKEN_ENV),
                issueNumber
        ).execute().body();
        return Objects.requireNonNull(responseBody).get("state").asText();
    }
}
