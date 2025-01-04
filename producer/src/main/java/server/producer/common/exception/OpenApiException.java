package server.producer.common.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import server.producer.common.exception.code.OpenApiErrorCode;


@Getter
@RequiredArgsConstructor
public class OpenApiException extends RuntimeException {
    private final OpenApiErrorCode errorCode;
}
