package graphmctwo.graph;

/**
 * 그래프 표시에 실패했을 때 던져지는 예외입니다.
 * @see Graph
 */
public class FailedToShowGraphException extends RuntimeException {
    /**
     * 특정 <code>cause</code>와 함께 새 {@link FailedToShowGraphException}을 던집니다. 이때 <code>cause</code>는 주로 {@link IllegalArgumentException}이 사용됩니다.
     *
     * @param cause {@link FailedToShowGraphException}의 원인
     */
    public FailedToShowGraphException(Throwable cause) {
        super(cause);
    }
}
