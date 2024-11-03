package graphmctwo.calculator;

import org.jetbrains.annotations.NotNull;

/**
 * 식(式)의 전반적인 처리를 담당하는 클래스입니다.
 */
public class ExpressionParser {
    /**
     * 제곱근 기호 상수입니다.
     */
    private static final String SQRT = "§c§o√§b";

    /**
     * <code>s</code>의 식에 색깔을 입히고 기호를 알아보기 편한 기호로 바꿔 디스플레이 식으로 변경합니다.
     * @param s 디스플레이 식으로 변경할 식
     * @return 디스플레이 식으로 변경된 <code>s</code>
     */
    public static @NotNull String toDisplayExpression(@NotNull String s) {
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

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private ExpressionParser() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
