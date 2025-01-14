package server.producer.common.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

	//200 OK
	MOVIE_TIMELINE_GET_SUCCESS(20001, "매물 조회 성공"),
	MOVIE_DETAIL_GET_SUCCESS(20002, "매물 세부 정보 조회 성공"),

	//201 CREATED
	MOVIE_BOOKING_POST_SUCCESS(20101, "입주 신청 성공");

	private final int code;
	private final String message;
}