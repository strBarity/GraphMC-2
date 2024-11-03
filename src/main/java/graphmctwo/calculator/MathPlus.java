package graphmctwo.calculator;

import graphmctwo.graph.GraphHandler;
import org.jetbrains.annotations.NotNull;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

/**
 * {@link Math} 클래스에는 없는, 그래프 계산에 필요한 특별한 수학적 기능이 포함된 클래스입니다.
 */
public class MathPlus {

    /**
     * 팩토리얼을 계산합니다. 감마 함수를 통해 음의 정수가 아닌 실수 전체에서의 범위에서 계산이 가능합니다.
     * @param d 팩토리얼을 계산할 실수
     * @return <code>d</code>에 대한 팩토리얼, <code>d</code>가 -1이 아닌 음의 정수인 경우 {@link Double#NaN} 반환,
     * -1인 경우 {@link Double#POSITIVE_INFINITY} 반환
     */
    public static double factorial(double d) {
        return gamma(d + 1);
    }

    /**
     * 감마 함수를 계산합니다. 감마 함수의 재귀적 정의와 Stirling 의 근사를 사용하여 계산합니다. (오차가 크긴 하지만 계산 시간의 단축을 위해 사용합니다.)
     * @param z 감마 함수를 계산할 실수
     * @return <code>d</code>에 대한 감마 함숫값, <code>d</code>가 0이 아닌 음의 정수인 경우 {@link Double#NaN} 반환,
     * 0인 경우 {@link Double#POSITIVE_INFINITY} 반환
     */
    public static double gamma(double z) {
        if (z < 0 && Math.floor(z) == z) {
            return Double.NaN;
        } else if (z < 0) {
            return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z)); // 감마 함수의 재귀적 정의 사용
        } else if (z == 0) {
            return Double.POSITIVE_INFINITY; // 감마(0)은 정의되지 않음
        } else {
            return stirlingApproximation(z); // z가 0보다 클 때, Stirling 의 근사를 사용하여 빠르게 계산
        }
    }

    /**
     * Stirling 의 근사를 사용해 0보다 큰 실수에 대한 감마 함수를 계산합니다.
     * @param z Stirling 의 근사를 사용할 1보다 큰 실수
     * @return Stirling 의 근사를 사용해 계산듼 z에 대한 감마 함숫값
     */
    private static double stirlingApproximation(double z) {
        if (z <= 0) throw new IllegalArgumentException("Stirling 근사에 사용되는 실수는 0보다 커야 합니다.");
        return Math.sqrt(2 * Math.PI / z) * Math.pow(z / Math.E, z);
    }

    /**
     * 소수점을 {@link GraphHandler#N}번째 자리 까지 반올림합니다.
     * @param value 소수점을 {@link GraphHandler#N}번째 자리까지 반올림할 실수
     * @return 소수점이 {@link GraphHandler#N}번째 자리까지 반올림된 <code>value</code>
     */
    public static double roundToNthDecimal(double value) {
        double scale = Math.pow(10, GraphHandler.getN());
        return Math.round(value * scale) / scale;
    }

    /**
     * 소수점을 <code>n</code>번째 자리 까지 반올림합니다.
     * @param value 소수점을 <code>n</code>번째 자리까지 반올림할 실수
     * @param n 반올림할 소수점의 자릿수
     * @return 소수점이 <code>n</code>번째 자리까지 반올림된 <code>value</code>
     */
    public static double roundToNthDecimal(double value, int n) {
        double scale = Math.pow(10, n);
        return Math.round(value * scale) / scale;
    }

    /**
     * 구간 [<code>from</code>, <code>to</code>]에서 <code>expression</code>을 <code>target</code>에 대해 정적분합니다.
     * 즉, <code>expression</code>의 그래프와 x축으로 둘러쌓인 부분의 넓이를 구합니다.
     * <code>expression</code>에 두 개의 그래프의 식의 차를 넣으면 두 개의 그래프로 둘러쌓인 부분의 넓이를 구합니다.
     * @param expression 정적분을 할 <code>target</code>에 대한 식
     * @param target 정적분을 할 문자, 주로 <code>x</code>를 사용
     * @param from 정적분을 할 시작점
     * @param to 정적분을 할 끝점
     * @return 정적분의 결과값 (<code>expression</code>과 x축으로 둘러쌓인 부분의 넓이, 또는
     * <code>expression</code>의 두 개의 그래프로 둘러쌓인 부분의 넓이)
     */
    public static double definiteIntegral(@NotNull String expression, @NotNull String target, double from, double to) {
        ExprEvaluator evaluator = new ExprEvaluator();
        expression = expression.replace("e", Double.toString(Math.E))
                .replace("pi", Double.toString(Math.PI));
        IExpr definiteIntegral = evaluator.eval("Integrate(" + expression + ", {" + target + ", " + from + ", " + to + "})");
        return Math.abs(definiteIntegral.toDoubleDefault());
    }
    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private MathPlus() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}
