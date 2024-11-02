package graphmctwo.calculator;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FunctionCalculator {

    private FunctionCalculator() {

    }

    private static final HashMap<String, Double> constants = new HashMap<>();

    static {
        // 상수 값 정의
        constants.put("pi", Math.PI);
        constants.put("e", Math.E);
    }

    public static double evaluate(String expression) {
        expression = replaceConstants(expression);
        expression = replaceAbsoluteValues(expression);
        expression = replaceFactorialNotation(expression);

        try {
            return new Parser(expression).parse();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("계산할 수 없는 수식: " + expression, e);
        }
    }

    private static String replaceConstants(String expression) {
        for (var entry : constants.entrySet()) {
            expression = expression.replace(entry.getKey(), entry.getValue().toString());
        }
        return expression;
    }

    private static String replaceAbsoluteValues(String expression) {
        // |d|를 abs(d)로 변환
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

    private static @NotNull String replaceFactorialNotation(String input) {
        // 정규 표현식 패턴: \(x\)! 형태를 찾기 위한 패턴
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)!");
        Matcher matcher = pattern.matcher(input);

        // 변환된 문자열을 저장할 StringBuilder
        StringBuilder result = new StringBuilder();
        int lastMatchEnd = 0;

        // 일치하는 부분을 찾아 변환
        while (matcher.find()) {
            result.append(input, lastMatchEnd, matcher.start()); // 일치 전의 부분 추가
            String xValue = matcher.group(1); // x 값 추출
            result.append("fact(").append(xValue).append(")"); // fact(x) 추가
            lastMatchEnd = matcher.end(); // 마지막 매치의 끝 위치 업데이트
        }

        // 남은 문자열 추가
        result.append(input.substring(lastMatchEnd));

        return result.toString();
    }

    private static class Parser {
        private final String expression;
        private int pos = 0;

        public Parser(String expression) {
            this.expression = expression.replaceAll("\\s+", "");
        }

        public double parse() {
            double result = parseExpression();
            if (pos < expression.length()) {
                throw new IllegalArgumentException("남은 문자: " + expression.substring(pos));
            }
            return result;
        }

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

        private double evaluateFunction(String functionName, double argument) {
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
}