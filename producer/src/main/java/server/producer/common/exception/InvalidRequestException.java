package server.producer.common.exception;

public class InvalidRequestException extends RuntimeException {
	public InvalidRequestException(String message) {
		super("유효하지 않는 인자입니다.");
	}
}
