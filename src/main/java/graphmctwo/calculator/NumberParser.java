package graphmctwo.calculator;

import graphmctwo.graph.Graph;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 숫자의 전반적인 처리를 담당하는 클래스입니다.
 */
public class NumberParser {

    /**
     * 입력받은 문자열이 {@link Double}이 아닌지 판단합니다.
     *
     * @param s {@link Double}인지 판단할 문자열
     * @return <code>s</code>가 {@link Double}이라면 <code>false</code>, 아니라면 <code>true</code> 반환
     */
    public static boolean isNotDouble(@NotNull String s) {
        try {
            Double.parseDouble(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * 입력받은 문자열이 {@link Float}이 아닌지 판단합니다.
     *
     * @param s {@link Float}인지 판단할 문자열
     * @return <code>s</code>가 {@link Float}이라면 <code>false</code>, 아니라면 <code>true</code> 반환
     */
    public static boolean isNotFloat(@NotNull String s) {
        try {
            Float.parseFloat(s);
            return false;
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * 문자열에 있는 모든 숫자를 위 첨자 숫자로 변경합니다. 주로 정적분{@link graphmctwo.graph.DefiniteIntegralHandler#processDefiniteIntegral(Player, Graph)}에 이용됩니다.
     *
     * @param s 위 첨자 숫자로 숫자를 변경할 문자열
     * @return 모든 숫자가 위 첨자로 변경된 <code>s</code>
     */
    public static @NotNull String toSuperScript(@NotNull String s) {
        return s.replace("0", "⁰").replace("1", "¹").replace("2", "²").replace("3", "³").replace("4", "⁴").replace("5", "⁵").replace("6", "⁶").replace("7", "⁷").replace("8", "⁸").replace("9", "⁹").replace(".", "·").replace("-", "⁻");
    }

    /**
     * 문자열에 있는 모든 숫자를 아래 첨자 숫자로 변경합니다. 주로 정적분{@link graphmctwo.graph.DefiniteIntegralHandler#processDefiniteIntegral(Player, Graph)}에 이용됩니다.
     *
     * @param s 아래 첨자 숫자로 숫자를 변경할 문자열
     * @return 모든 숫자가 아래 첨자로 변경된 <code>s</code>
     */
    public static @NotNull String toSubScript(@NotNull String s) {
        return s.replace("0", "₀").replace("1", "₁").replace("2", "₂").replace("3", "₃").replace("4", "₄").replace("5", "₅").replace("6", "₆").replace("7", "₇").replace("8", "₈").replace("9", "₉").replace("-", "₋");
    }

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     *
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private NumberParser() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
