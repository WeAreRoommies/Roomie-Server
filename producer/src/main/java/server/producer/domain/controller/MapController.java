package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.common.exception.InvalidRequestException;
import server.producer.common.util.SecurityUtil;
import server.producer.domain.dto.request.FilterRequestDto;
import server.producer.domain.dto.response.FilterResponseDto;
import server.producer.domain.service.MapService;

@RestController
@RequestMapping("/v1/maps")
@RequiredArgsConstructor
public class MapController {
	private final MapService mapService;

	@PostMapping("/search")
	public ApiResponseDto<FilterResponseDto> searchPropertiesOnMap(@RequestBody FilterRequestDto requestDto){
		try {
			Long userId = SecurityUtil.getCurrentUserId();
			FilterResponseDto responseDto = mapService.searchProperties(requestDto, userId);
			return ApiResponseDto.success(SuccessCode.MAP_SEARCH_SUCCESS, responseDto);
		} catch (IllegalArgumentException e) {
			FilterResponseDto responseDto = FilterResponseDto.builder()
				.houses(new ArrayList<>())
				.latitude(37.55348)
				.longitude(126.9381)
				.build();
			return ApiResponseDto.success(SuccessCode.MAP_SEARCH_REJECT, responseDto);
		} catch (InvalidRequestException invalidRequestException) {
			return ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
