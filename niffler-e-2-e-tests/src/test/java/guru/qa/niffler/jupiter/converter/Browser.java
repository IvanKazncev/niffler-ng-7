package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Browser {
    CHROME(getConfig("chrome")),
    FIREFOX(getConfig("firefox")),;

    private final SelenideConfig selenideConfig;

    private static SelenideConfig getConfig(String browser) {
        return new SelenideConfig()
                .browser(browser)
                .pageLoadStrategy("eager");
    }

}
