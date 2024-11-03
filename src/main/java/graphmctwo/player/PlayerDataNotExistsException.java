package graphmctwo.player;

/**
 * 예기치 못한 상황에 플레이어의 데이터가 없는 경우 던져지는 예외입니다.
 */
public class PlayerDataNotExistsException extends RuntimeException {
    /**
     * 특정 메시지와 함께 {@link PlayerDataNotExistsException}을 던집니다.
     * @param message 예외와 함께 출력할 메시지
     */
    public PlayerDataNotExistsException(String message) {
        super(message);
    }
}
