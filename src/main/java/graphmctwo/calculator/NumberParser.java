package graphmctwo.calculator;

import org.jetbrains.annotations.NotNull;

public class NumberParser {
    private NumberParser() {

    }

    public static boolean isNotDouble(String s) {
        try {
            Double.parseDouble(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static boolean isNotFloat(String s) {
        try {
            Float.parseFloat(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    public static @NotNull String toSuperScript(@NotNull String s) {
        return s.replace("0", "⁰")
                .replace("1", "¹")
                .replace("2", "²")
                .replace("3", "³")
                .replace("4", "⁴")
                .replace("5", "⁵")
                .replace("6", "⁶")
                .replace("7", "⁷")
                .replace("8", "⁸")
                .replace("9", "⁹")
                .replace(".", "·")
                .replace("-", "⁻");
    }

    public static @NotNull String toSubScript(@NotNull String s) {
        return s.replace("0", "₀")
                .replace("1", "₁")
                .replace("2", "₂")
                .replace("3", "₃")
                .replace("4", "₄")
                .replace("5", "₅")
                .replace("6", "₆")
                .replace("7", "₇")
                .replace("8", "₈")
                .replace("9", "₉")
                .replace("-", "₋");
    }
}
