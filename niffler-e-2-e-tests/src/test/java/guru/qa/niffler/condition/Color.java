package guru.qa.niffler.condition;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


@AllArgsConstructor
public enum Color {
    yellow("rgba(255, 183, 3, 1)"),
    green("rgba(53, 173, 123, 1)"),
    orange("rgba(251, 133, 0, 1)"),
    blue("rgba(41, 65, 204, 1)");


    public final String rgb;


    @NotNull
    public static Color fromRgba(String rgba) {
        Color color = Arrays.stream(Color.values()).filter(x->x.rgb.equals(rgba)).findFirst().orElse(null);
        if (color == null) {
            throw new IllegalArgumentException("Указан некорректный код цвета  " + rgba);
        }
        return color;
    }
}
