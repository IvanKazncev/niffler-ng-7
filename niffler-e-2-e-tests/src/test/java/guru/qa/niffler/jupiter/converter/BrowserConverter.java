package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserConverter implements ArgumentConverter {
    @Override
    public Object convert(Object source, ParameterContext context) throws ArgumentConversionException {
        if (context.getParameter().getType() != SelenideDriver.class) {
            throw new ArgumentConversionException("Target type must be SelenideDriver");
        }

        return switch (source.toString()) {
            case "chrome" -> new SelenideDriver(Browser.CHROME.getSelenideConfig());
            case "firefox" -> new SelenideDriver(Browser.FIREFOX.getSelenideConfig());
            default -> throw new ArgumentConversionException("Unsupported browser type: " + source);
        };
    }
}
