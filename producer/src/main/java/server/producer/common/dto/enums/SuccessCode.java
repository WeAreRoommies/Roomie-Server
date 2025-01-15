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
	PINNED_HOUSES_GET_SUCCESS(20005, "찜 리스트 조회 성공"),
	MAIN_PAGE_GET_SUCCESS(20003, "메인페이지 조회 성공"),
	MY_PAGE_GET_SUCCESS(20004, "마이페이지 조회 성공"),

	//201 CREATED
	ROOM_REQUEST_POST_SUCCESS(20101, "입주 신청 성공");

	private final int code;
	private final String message;
}