package graphmctwo.calculator;

import org.jetbrains.annotations.NotNull;

public class ExpressionParser {
    private static final String SQRT = "§c§o√§b";

    private ExpressionParser() {

    }

    public static @NotNull String userFunction(@NotNull String s) {
        return s.replace("x", "§dx§b")
                .replace("e", "§5e§b")
                .replace("pi", "§5π§b")
                .replace("π", "§5π§b")
                .replace("sinh", "§2si§2nh§b")
                .replace("cosh", "§2co§2sh§b")
                .replace("tanh", "§2ta§2nh§b")
                .replace("asin", "§aasi§an§b")
                .replace("acos", "§aaco§as§b")
                .replace("atan", "§aata§an§b")
                .replace("sin", "§esin§b")
                .replace("cos", "§ecos§b")
                .replace("tan", "§etan§b")
                .replace("§5§oe§b§dx§bp", "§3exp§b")
                .replace("log", "§6log§b")
                .replace("ln", "§6ln§b")
                .replace("abs", "§4abs§b")
                .replace("|", "§4|§b")
                .replace("√", SQRT)
                .replace("root", SQRT)
                .replace("sqrt", SQRT)
                .replace("gamma", "§9§lΓ§b")
                .replace("fact", "§9fact§b");
    }
}
