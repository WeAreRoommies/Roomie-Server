package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.domain.dto.response.MoodHouseResponseDto;
import server.producer.domain.service.HouseService;

@RestController
@RequestMapping("/v1/houses")
@RequiredArgsConstructor
public class HouseController {
	private final HouseService houseService;
	private final Long userId = 1L;

	@GetMapping
	public ApiResponseDto<MoodHouseResponseDto> getHousesByMoodAndLocation(@RequestParam String moodTag) {
		try {
			MoodHouseResponseDto moodHousesDto = houseService.getHousesByMoodAndLocation(moodTag, userId);
			return ApiResponseDto.success(SuccessCode.HOUSE_GET_SUCCESS, moodHousesDto);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.NOT_FOUND_HOUSE);
		}
	}
}
