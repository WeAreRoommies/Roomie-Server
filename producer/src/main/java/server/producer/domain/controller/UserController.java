package server.producer.domain.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.common.util.SecurityUtil;
import server.producer.domain.dto.request.*;
import server.producer.domain.dto.response.*;
import server.producer.domain.service.UserService;
import server.producer.security.jwt.JwtTokenProvider;
import server.producer.security.jwt.RefreshTokenRepository;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;

	@GetMapping("/home")
	public ApiResponseDto<HomeInfoResponseDto> getUserHomeInfo(HttpServletRequest request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			HomeInfoResponseDto userHomeInfo = userService.getUserInfoAndRecentlyViewedHouse(userId);
			return ApiResponseDto.success(SuccessCode.MAIN_PAGE_GET_SUCCESS, userHomeInfo);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.USER_NOT_FOUND);
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

	@GetMapping("/mypage/accountinfo")
	public ApiResponseDto<AccountInfoResponseDto> getAccountInfo() {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			AccountInfoResponseDto dto = userService.getAccountInfo(userId);
			return ApiResponseDto.success(SuccessCode.ACCOUNT_INFO_GET_SUCCESS, dto);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@PatchMapping("/nickname")
	public ApiResponseDto<NicknameUpdateResponseDto> updateNickname(@RequestBody NicknameUpdateRequestDto request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			userService.updateNickname(userId, request.nickname());
			NicknameUpdateResponseDto responseDto = new NicknameUpdateResponseDto(request.nickname());
			return ApiResponseDto.success(SuccessCode.USER_UPDATE_SUCCESS, responseDto);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@PatchMapping("/name")
	public ApiResponseDto<NameUpdateResponseDto> updateName(@RequestBody NameUpdateRequestDto request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			userService.updateName(userId, request.name());
			NameUpdateResponseDto responseDto = new NameUpdateResponseDto(request.name());
			return ApiResponseDto.success(SuccessCode.USER_UPDATE_SUCCESS, responseDto);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@PatchMapping("/birthday")
	public ApiResponseDto<BirthDayUpdateResponseDto> updateBirthDay(@RequestBody BirthDayUpdateRequestDto request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			userService.updateBirthDay(userId, request.birthDay());
			BirthDayUpdateResponseDto responseDto = new BirthDayUpdateResponseDto(request.birthDay());
			return ApiResponseDto.success(SuccessCode.USER_UPDATE_SUCCESS, responseDto);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@PatchMapping("/phonenumber")
	public ApiResponseDto<PhoneNumberUpdateResponseDto> updatePhoneNumber(@RequestBody PhoneNumberUpdateRequestDto request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			userService.updatePhoneNumber(userId, request.phoneNumber());
			PhoneNumberUpdateResponseDto responseDto = new PhoneNumberUpdateResponseDto(request.phoneNumber());
			return ApiResponseDto.success(SuccessCode.USER_UPDATE_SUCCESS, responseDto);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@PatchMapping("/gender")
	public ApiResponseDto<GenderUpdateResponseDto> updateGender(@RequestBody GenderUpdateRequestDto request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			userService.updateGender(userId, request.gender());
			GenderUpdateResponseDto responseDto = new GenderUpdateResponseDto(request.gender());
			return ApiResponseDto.success(SuccessCode.USER_UPDATE_SUCCESS, responseDto);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping("/delete")
	public ApiResponseDto<Void> deleteUser(@RequestHeader("Authorization") String refreshTokenHeader) {
		try {
			if (refreshTokenHeader == null || !refreshTokenHeader.startsWith("Bearer ")) {
				return ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_HEADER);
			}
			String refreshToken = refreshTokenHeader.substring(7);

			// 1. 토큰 유효성 검사
			if (!jwtTokenProvider.validateToken(refreshToken)) {
				return ApiResponseDto.fail(ErrorCode.INVALID_REFRESH_TOKEN);
			}

			// 2. 토큰 저장소에서 userId 조회
			Long userId = refreshTokenRepository.findUserIdByToken(refreshToken).orElse(null);
			if (userId == null) {
				return ApiResponseDto.fail(ErrorCode.INVALID_REFRESH_TOKEN);
			}

			// 3. 토큰 삭제
			refreshTokenRepository.deleteByToken(refreshToken);

			// 4. 유저 삭제
			userService.deleteUser(userId);

			return ApiResponseDto.success(SuccessCode.USER_DELETE_SUCCESS, null);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@PatchMapping("/location")
	public ApiResponseDto<LocationUpdateResponseDto> updateLocation(@RequestBody LocationUpdateRequestDto request) {
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			LocationUpdateResponseDto responseDto = userService.updateLocation(userId, request);
			return ApiResponseDto.success(SuccessCode.USER_UPDATE_SUCCESS, responseDto);
		} catch (EntityNotFoundException e) {
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (IllegalArgumentException e) {
			return ApiResponseDto.success(SuccessCode.USER_UPDATE_REJECT, null);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
