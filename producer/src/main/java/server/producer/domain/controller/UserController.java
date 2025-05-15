package server.producer.domain.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.common.util.SecurityUtil;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.dto.response.MyPageResponseDto;
import server.producer.domain.service.UserService;
import server.producer.security.jwt.JwtTokenProvider;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@GetMapping("/home")
	public ApiResponseDto<HomeInfoResponseDto> getUserHomeInfo(HttpServletRequest request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			HomeInfoResponseDto userHomeInfo = userService.getUserInfoAndRecentlyViewedHouse(userId);
			return ApiResponseDto.success(SuccessCode.MAIN_PAGE_GET_SUCCESS, userHomeInfo);
		} catch (InvalidParameterException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER); // userId가 유효하지 않은 경우
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR); // 기타 예상치 못한 예외
		}
	}

    @GetMapping("/mypage")
    public ApiResponseDto<MyPageResponseDto> getMyPage(HttpServletRequest request) {
        try{
			Long userId = SecurityUtil.getCurrentUserId();
            MyPageResponseDto userMyPage = userService.getMyPage(userId);
            return ApiResponseDto.success(SuccessCode.MY_PAGE_GET_SUCCESS, userMyPage);
        } catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER); // userId가 유효하지 않은 경우
		} catch (Exception e){
            return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
