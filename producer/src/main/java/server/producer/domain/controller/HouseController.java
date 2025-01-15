package server.producer.domain.controller;

<<<<<<< HEAD
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
=======
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
>>>>>>> 163576d776bf6e995e70762e4b20f1a484fc80fb
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
<<<<<<< HEAD
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.dto.response.HouseDetailsResponseDto;
=======
import server.producer.domain.dto.response.MoodHouseResponseDto;
>>>>>>> 163576d776bf6e995e70762e4b20f1a484fc80fb
import server.producer.domain.service.HouseService;

@RestController
@RequestMapping("/v1/houses")
<<<<<<< HEAD
public class HouseController {
    private final HouseService houseService;
    public HouseController(HouseService houseService) {
        this.houseService = houseService;
    }
    private final Long userId = 1L;

    @GetMapping("/{houseId}/details")
    public ApiResponseDto<HouseDetailsResponseDto> getHouseDetails(@PathVariable Long houseId) {
        try{
            HouseDetailsResponseDto responseDto = houseService.getHouseDetails(houseId, userId);
            return ApiResponseDto.success(SuccessCode.HOUSE_GET_SUCCESS, responseDto);
        } catch (Exception e){
            return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

=======
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
>>>>>>> 163576d776bf6e995e70762e4b20f1a484fc80fb
}
