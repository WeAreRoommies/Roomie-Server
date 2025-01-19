package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.common.exception.InvalidRequestException;
import server.producer.domain.dto.request.FilterRequestDto;
import server.producer.domain.dto.response.FilterResponseDto;
import server.producer.domain.service.MapService;

@RestController
@RequestMapping("/v1/maps")
@RequiredArgsConstructor
public class MapController {
	private final MapService mapService;
	private final Long userId = 1L;

	@GetMapping("/search")
	public ApiResponseDto<FilterResponseDto> searchPropertiesOnMap(@RequestBody FilterRequestDto requestDto){
		try {
			FilterResponseDto responseDto = mapService.searchProperties(requestDto, userId);
			return ApiResponseDto.success(SuccessCode.HOUSE_GET_SUCCESS, responseDto);
		} catch (InvalidRequestException invalidRequestException) {
			return ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
