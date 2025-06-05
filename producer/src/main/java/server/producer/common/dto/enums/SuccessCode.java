package server.producer.common.dto.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessCode {

	//200 OK
	HOUSE_GET_SUCCESS(20001, "매물 조회 성공"),
	HOUSE_DETAIL_GET_SUCCESS(20002, "매물 세부 정보 조회 성공"),
	MAIN_PAGE_GET_SUCCESS(20003, "메인페이지 조회 성공"),
	MY_PAGE_GET_SUCCESS(20004, "마이페이지 조회 성공"),
	PINNED_HOUSES_GET_SUCCESS(20005, "찜 리스트 조회 성공"),
	PIN_TOGGLE_SUCCESS(20006, "찜하기/찜취소 성공"),
	LOCATION_SUCCESS(20007, "지도 검색 성공"),
	SOCIAL_LOGIN_SUCCESS(20008, "소셜 로그인 성공"),
	TOKEN_REISSUE_SUCCESS(20009, "토큰 재발급 성공"),
	USER_UPDATE_SUCCESS(20010, "회원 정보 수정 성공"),
	ACCOUNT_INFO_GET_SUCCESS(20011, "계정 정보 조회 성공"),
	LOGOUT_SUCCESS(20012, "로그아웃 성공"),

	//201 CREATED
	ROOM_REQUEST_POST_SUCCESS(20101, "입주 신청 성공"),
	SOCIAL_SIGNUP_SUCCESS(20102, "소셜 회원가입 성공");

	private final int code;
	private final String message;
}