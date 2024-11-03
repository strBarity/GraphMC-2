package graphmctwo.graph;

/**
 * 비정상적인 정적분을 시도했을 때 던져지는 예외입니다.
 * 예를 들어, y=g1(x)와 y=g2(x)로 둘러쌓인 부분에 대해 정적분을 실행할 때,
 * 현재 x값에 대해 g1(x) > g2(x)이면 y = g2(x)부터 시작해 g1(x)까지 늘어나며
 * 입자를 표시해야 하는데, y = g1(x)부터 시작해 g2(x)까지 늘어나려고 시도하는 경우에 이 예외가 던져집니다.
 * (이 경우 y값은 한없이 커지며 영원히 계산을 실행하기 때문에 서버가 멈추게 됩니다.)
 */
public class IllegalDefiniteIntegralException extends RuntimeException {
    /**
     * 특정 메시지와 함께 {@link IllegalDefiniteIntegralException}을 던집니다.
     * @param message 예외와 함께 출력할 메시지
     */
    public IllegalDefiniteIntegralException(String message) {
        super(message);
    }
}
