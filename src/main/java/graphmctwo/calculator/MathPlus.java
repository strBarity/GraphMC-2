package graphmctwo.calculator;

import graphmctwo.graph.GraphHandler;
import org.matheclipse.core.eval.ExprEvaluator;
import org.matheclipse.core.interfaces.IExpr;

public class MathPlus {
    private MathPlus() {

    }

    public static double factorial(double d) {
        return gamma(d + 1);
    }

    public static double gamma(double z) {
        if (z <= 0 && Math.floor(z) == z) {
            return Double.NaN;
        }

        // 감마 함수의 재귀적 정의 사용
        if (z < 0) {
            return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z));
        } else if (z == 0) {
            return Double.POSITIVE_INFINITY; // 감마(0)은 정의되지 않음
        } else {
            // 감마 함수의 값이 1보다 클 때, Stirling 의 근사를 사용하여 빠르게 계산
            return stirlingApproximation(z);
        }
    }

    // Stirling 근사
    private static double stirlingApproximation(double z) {
        return Math.sqrt(2 * Math.PI / z) * Math.pow(z / Math.E, z);
    }

    public static double roundToNthDecimal(double value) {
        double scale = Math.pow(10, GraphHandler.getN());
        return Math.round(value * scale) / scale;
    }

    public static double roundToNthDecimal(double value, int n) {
        double scale = Math.pow(10, n);
        return Math.round(value * scale) / scale;
    }

    public static double definiteIntegral(String expression, String target, double from, double to) {
        ExprEvaluator evaluator = new ExprEvaluator();
        expression = expression.replace("e", Double.toString(Math.E))
                .replace("pi", Double.toString(Math.PI));
        IExpr definiteIntegral = evaluator.eval("Integrate(" + expression + ", {" + target + ", " + from + ", " + to + "})");
        return Math.abs(definiteIntegral.toDoubleDefault());
    }
}
