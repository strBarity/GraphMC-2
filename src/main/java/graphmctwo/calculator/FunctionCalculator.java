package graphmctwo.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 수식을 계산하는 클래스입니다.
 */
public class FunctionCalculator {
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
     * 주어진 수식 문자열을 입력받아, 최종적인 계산 결과를 반환합니다.
     * @param expression 계산할 수식
     * @return <code>expression</code>을 계산한 결과
     */
    public static double evaluate(@NotNull String expression) {
        expression = replaceConstants(expression);
        expression = replaceAbsoluteValues(expression);
        expression = replaceFactorialNotation(expression);
        try {
            return new Parser(expression).parse();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("계산할 수 없는 수식: " + expression, e);
        }
    }

    /**
     * 수식 문자열에 포함된 수학 상수를 해당하는 상수 값으로 변경합니다.
     * @param expression 상수를 상수 값으로 변경할 문자열
     * @return 상수 값으로 변경된 문자열
     */
    private static @NotNull String replaceConstants(@NotNull String expression) {
        for (Map.Entry<String, Double> entry : constants.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue().toString());
        }
        return expression;
    }

    /**
     * 수식 문자열에 포함된 |d| 꼴의 절댓값 수식을 abs(d)로 변환합니다.
     * @param expression |d| 꼴을 abs(d)로 변경할 문자열
     * @return 모든 |d| 꼴이 abs(d)꼴로 변경된 문자열
     */
    private static @NotNull String replaceAbsoluteValues(@NotNull String expression) {
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
     * @param input (x)!꼴을 fact(x)로 바꿀 문자열
     * @return (x)!꼴이 fact(x)로 변경된 문자열
     */
    private static @NotNull String replaceFactorialNotation(@NotNull String input) {
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
     * 최종적으로 수식을 파싱하고 계산해 결괏값을 도출하는 데 사용되는 모든 주요 로직을 담당하는 클래스입니다.
     */
    private static class Parser {
        /**
         * 파싱할 수식입니다.
         */
        private final String expression;
        /**
         * 현재 위치를 추적하는 데 사용되는 상수입니다.
         */
        private int pos = 0;

        /**
         * 새로운 수식을 파싱하는 Parser 를 생성합니다.
         * @param expression 파싱하는 데에 이용될 수식
         */
        public Parser(@NotNull String expression) {
            this.expression = expression.replaceAll("\\s+", "");
        }

        /**
         * 수식을 처음부터 끝까지 파싱해 계산 값을 도출해냅니다.
         * @return 계산된 {@link Parser#expression}의 결과값
         * @throws IllegalArgumentException 수식을 끝까지 계산하지 못하고 남은 문자가 있는 경우
         */
        public double parse() {
            double result = parseExpression();
            if (pos < expression.length()) {
                throw new IllegalArgumentException("남은 문자: " + expression.substring(pos));
            }
            return result;
        }

        /**
         * {@link Parser#parseTerm()}을 이용해 우선순위가 높은 계산을 먼저 처리하고,
         * +, - 연산자를 기준으로 계산을 수행해 우선순위에 따라 덧셈, 뺄셈을 처리하며
         * 최종적으로 모든 계산이 완료된 {@link Parser#expression}을 반환합니다.
         * @return 함수, 괄호, 제곱, 곱셈, 나눗셈, 덧셈, 뺄셈의 계산 과정이 모두 처리된 {@link Parser#expression}
         */
        private double parseExpression() {
            double result = parseTerm();
            while (pos < expression.length()) {
                char op = expression.charAt(pos);
                if (op == '+' || op == '-') {
                    pos++;
                    result = applyOp(result, op, parseTerm());
                } else {
                    break;
                }
            }
            return result;
        }

        /**
         * {@link Parser#parseFactor()}를 통해 우선순위가 높은 계산을 먼저 처리하고,
         * *, / 연산자를 기준으로 계산을 수행해 우선순위에 따라 곱셈, 나눗셈을 처리합니다.
         * @return 함수, 괄호, 제곱, 곱셈, 나눗셈이 모두 처리된 {@link Parser#expression}
         */
        private double parseTerm() {
            double result = parseFactor();
            while (pos < expression.length()) {
                char op = expression.charAt(pos);
                if (op == '*' || op == '/') {
                    pos++;
                    result = applyOp(result, op, parseFactor());
                } else {
                    break;
                }
            }
            return result;
        }

        /**
         * {@link Parser#parseBase()}를 통해 우선순위가 높은 계산을 먼저 처리하고,
         * ^ 연선자를 기준으로 계산을 수행해 제곱 계산을 처리합니다.
         * @return 함수, 괄호, 제곱이 모두 처리된 {@link Parser#expression}
         */
        private double parseFactor() {
            double result = parseBase();
            while (pos < expression.length()) {
                char op = expression.charAt(pos);
                if (op == '^') {
                    pos++;
                    result = Math.pow(result, parseBase());
                } else {
                    break;
                }
            }
            return result;
        }

        /**
         * 괄호 안의 수식을 {@link Parser#parseExpression()}을 사용한 재귀적 정의를 통해 계산합니다.
         * 이때 {@link Parser#parseNumber()}와 {@link Parser#parseFunction()}을 사용해 숫자 및 소수점을 double로 읽고,
         * {@link Parser#expression} 안에 있는 모든 함수를 계산합니다.
         * @return 함수, 괄호가 모두 계산된 {@link Parser#expression}
         * @throws IllegalArgumentException 수식이 잘못되었거나 괄호가 닫히지 않은 경우
         */
        private double parseBase() {
            if (pos >= expression.length()) {
                throw new IllegalArgumentException("잘못된 수식");
            }

            char currentChar = expression.charAt(pos);
            if (currentChar == '(') {
                pos++;
                double result = parseExpression();
                if (pos < expression.length() && expression.charAt(pos) == ')') {
                    pos++;
                } else {
                    throw new IllegalArgumentException("괄호가 닫히지 않음");
                }
                return result;
            } else if (Character.isDigit(currentChar) || currentChar == '-') {
                return parseNumber();
            } else if (Character.isLetter(currentChar)) {
                return parseFunction();
            } else {
                throw new IllegalArgumentException("잘못된 문자: " + currentChar);
            }
        }

        /**
         * 수식 내의 숫자와 소수점을 읽어 double로 반환합니다.
         * @return {@link Parser#expression}의 숫자 및 소수점을 double로 읽은 값
         * @throws IllegalArgumentException 숫자가 잘못된 경우
         */
        private double parseNumber() {
            StringBuilder sb = new StringBuilder();
            boolean hasDot = false;
            if (expression.charAt(pos) == '-') {
                sb.append('-');
                pos++;
            }
            while (pos < expression.length()) {
                char currentChar = expression.charAt(pos);
                if (Character.isDigit(currentChar)) {
                    sb.append(currentChar);
                    pos++;
                } else if (currentChar == '.') {
                    if (hasDot) break;
                    hasDot = true;
                    sb.append(currentChar);
                    pos++;
                } else {
                    break;
                }
            }
            try {
                return Double.parseDouble(sb.toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("유효한 숫자가 아닙니다.");
            }
        }

        /**
         * {@link Parser#expression} 안에 있는 모든 함수를 {@link Parser#evaluateFunction(String, double)}를 통해 계산합니다.
         * @return 모든 함수가 계산된 {@link Parser#expression}
         * @throws IllegalArgumentException 괄호가 닫히지 않았거나 알 수 없는(지원하지 않는) 함수 또는 잘못된 함수가 있는 경우
         */
        private double parseFunction() {
            StringBuilder sb = new StringBuilder();
            while (pos < expression.length() && Character.isLetter(expression.charAt(pos))) {
                sb.append(expression.charAt(pos));
                pos++;
            }
            String functionName = sb.toString();
            if (expression.charAt(pos) == '(') {
                pos++;
                double argument = parseExpression();
                if (pos < expression.length() && expression.charAt(pos) == ')') {
                    pos++;
                } else {
                    throw new IllegalArgumentException("괄호가 닫히지 않음");
                }
                return evaluateFunction(functionName, argument);
            }
            throw new IllegalArgumentException("잘못된 함수: " + functionName);
        }

        /**
         * {@link Math} 클래스와 {@link MathPlus} 클래스를 이용해 함숫값을 계산합니다.
         * @param functionName 함수의 이름
         * @param argument 함숫값을 도출할 실수
         * @return 계산된 함숫값
         * @throws IllegalArgumentException <code>functionName</code>이 알 수 없는(지원하지 않는) 함수인 경우
         */
        private double evaluateFunction(@NotNull String functionName, double argument) {
            return switch (functionName) {
                case "sin" -> Math.sin(argument);
                case "cos" -> Math.cos(argument);
                case "tan" -> Math.tan(argument);
                case "asin" -> Math.asin(argument);
                case "acos" -> Math.acos(argument);
                case "atan" -> Math.atan(argument);
                case "sinh" -> Math.sinh(argument);
                case "cosh" -> Math.cosh(argument);
                case "tanh" -> Math.tanh(argument);
                case "abs" -> Math.abs(argument);
                case "log" -> Math.log10(argument);
                case "ln" -> Math.log(argument);
                case "sqrt" -> Math.sqrt(argument);
                case "gamma" -> MathPlus.gamma(argument);
                case "fact", "!" -> MathPlus.factorial(argument);
                default -> throw new IllegalArgumentException("지원하지 않는 함수: " + functionName);
            };
        }

        /**
         * 두 피연산자와 연산자를 사용해 사칙연산을 수행합니다.
         * +, -, *, /을 처리하며 0으로 나누기를 시도할 경우 {@link Double#NaN}을 반환합니다.
         * @param left 피연산자
         * @param op 연산 기호
         * @param right 연산자
         * @return <code>left</code>를 <code>right</code>로 <code>op</code>한 값
         */
        private double applyOp(double left, char op, double right) {
            return switch (op) {
                case '+' -> left + right;
                case '-' -> left - right;
                case '*' -> left * right;
                case '/' -> {
                    if (right == 0) {
                        yield Double.NaN; // 0으로 나누기
                    }
                    yield left / right;
                }
                default -> throw new IllegalArgumentException("지원하지 않는 연산자: " + op);
            };
        }
    }
    /**
     * 불필요한 인스턴스화를 방지하기 위한 Private 생성자입니다.
     * @throws IllegalStateException 클래스를 인스턴스화했을 때
     */
    private FunctionCalculator() {
        throw new IllegalStateException("이 클래스는 인스턴스화할 수 없습니다.");
    }
}