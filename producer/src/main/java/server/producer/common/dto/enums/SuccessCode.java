package server.producer.common.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessCode {

	//200 OK
	HOUSE_GET_SUCCESS(20001, "매물 조회 성공"),
	ROOM_DETAIL_GET_SUCCESS(20002, "매물 세부 정보 조회 성공"),

	//201 CREATED
	ROOM_REQUEST_POST_SUCCESS(20101, "입주 신청 성공");

	private final int code;
	private final String message;
}