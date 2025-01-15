package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.service.UserService;

@RestController("/v1/users")
@RequiredArgsConstructor
public class UserController {
	private final Long userId = 1L;
	private final UserService userService;

	@GetMapping("/home")
	public ApiResponseDto<HomeInfoResponseDto> getUserHomeInfo() {
		try{
			HomeInfoResponseDto userHomeInfo = userService.getUserInfoAndRecentlyViewedHouse(userId);
			return ApiResponseDto.success(SuccessCode.ROOM_DETAIL_GET_SUCCESS, userHomeInfo);
		} catch (Exception e){
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
