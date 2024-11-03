package graphmctwo.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 식(式)의 전반적인 처리를 담당하는 클래스입니다.
 */
public class ExpressionParser {
    /**
     * 수학 상수가 저장된 {@link Map}입니다.
     */
    private static final HashMap<String, Double> constants = new HashMap<>();

    /*
     수학 상수 저장, 나중에 추가될 수 있음
     */
    static {
        constants.put("pi", Math.PI);
        constants.put("e", Math.E);
    }

    /**
     * 제곱근 기호 상수입니다.
     */
    private static final String SQRT = "§c§o√§b";

    /**
     * <code>s</code>의 식에 색깔을 입히고 기호를 알아보기 편한 기호로 바꿔 디스플레이 식으로 변경합니다.
     *
     * @param s 디스플레이 식으로 변경할 식
     * @return 디스플레이 식으로 변경된 <code>s</code>
     */
    public static @NotNull String toDisplayExpression(@NotNull String s) {
        return s.replace("x", "§dx§b").replace("e", "§5e§b").replace("pi", "§5π§b").replace("π", "§5π§b").replace("sinh", "§2si§2nh§b").replace("cosh", "§2co§2sh§b").replace("tanh", "§2ta§2nh§b").replace("asin", "§aasi§an§b").replace("acos", "§aaco§as§b").replace("atan", "§aata§an§b").replace("sin", "§esin§b").replace("cos", "§ecos§b").replace("tan", "§etan§b").replace("§5§oe§b§dx§bp", "§3exp§b").replace("log", "§6log§b").replace("ln", "§6ln§b").replace("abs", "§4abs§b").replace("|", "§4|§b").replace("√", SQRT).replace("root", SQRT).replace("sqrt", SQRT).replace("gamma", "§9§lΓ§b").replace("fact", "§9fact§b");
    }

    /**
     * 수식 문자열에 포함된 수학 상수를 해당하는 상수 값으로 변경합니다.
     *
     * @param expression 상수를 상수 값으로 변경할 문자열
     * @return 상수 값으로 변경된 문자열
     */
    public static @NotNull String replaceConstants(@NotNull String expression) {
        for (Map.Entry<String, Double> entry : constants.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue().toString());
        }
        return expression;
    }

    /**
     * 수식 문자열에 포함된 |d| 꼴의 절댓값 수식을 abs(d)로 변환합니다.
     *
     * @param expression |d| 꼴을 abs(d)로 변경할 문자열
     * @return 모든 |d| 꼴이 abs(d)꼴로 변경된 문자열
     */
    public static @NotNull String replaceAbsoluteValues(@NotNull String expression) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < expression.length()) {
            if (expression.charAt(i) == '|') {
                int end = expression.indexOf('|', i + 1);
                if (end != -1) {
                    result.append("abs(");
                    result.append(expression, i + 1, end);
                    result.append(")");
                    i = end + 1;
                } else {
                    throw new IllegalArgumentException("잘못된 절댓값 표기: " + expression);
                }
            } else {
                result.append(expression.charAt(i));
                i++;
            }
        }
        return result.toString();
    }

    /**
     * 수식 문자열에 포함된 (x)!꼴의 문자열을 fact(x)로 변경합니다.
     *
     * @param input (x)!꼴을 fact(x)로 바꿀 문자열
     * @return (x)!꼴이 fact(x)로 변경된 문자열
     */
    public static @NotNull String replaceFactorialNotation(@NotNull String input) {
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)!");
        Matcher matcher = pattern.matcher(input);
        StringBuilder result = new StringBuilder();
        int lastMatchEnd = 0;
        while (matcher.find()) {
            result.append(input, lastMatchEnd, matcher.start()); // 일치 전의 부분 추가
            String xValue = matcher.group(1); // x 값 추출
            result.append("fact(").append(xValue).append(")"); // fact(x) 추가
            lastMatchEnd = matcher.end(); // 마지막 매치의 끝 위치 업데이트
        }
        result.append(input.substring(lastMatchEnd));
        return result.toString();
    }

    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     *
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private ExpressionParser() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
