package server.producer.domain.controller;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.common.util.SecurityUtil;
import server.producer.domain.dto.response.*;
import server.producer.domain.service.HouseService;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("/v1/houses")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;

    @GetMapping("/{houseId}/details")
    public ApiResponseDto<HouseDetailsResponseDto> getHouseDetails(@PathVariable Long houseId) {
        try{
            Long userId = SecurityUtil.getCurrentUserId();
            HouseDetailsResponseDto responseDto = houseService.getHouseDetails(houseId, userId);
            return ApiResponseDto.success(SuccessCode.HOUSE_DETAIL_GET_SUCCESS, responseDto);
        } catch (EntityNotFoundException e) {
            return ApiResponseDto.fail(ErrorCode.HOUSE_NOT_FOUND);
        } catch (InvalidParameterException e) {
            return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
        } catch (Exception e){
            return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
	@GetMapping
	public ApiResponseDto<MoodHouseResponseDto> getHousesByMoodAndLocation(@RequestParam String moodTag) {
		try {
            // moodTag가 없는 경우 에러 처리
            if (moodTag == null || moodTag.isEmpty()) {
                return ApiResponseDto.fail(ErrorCode.MISSING_REQUIRED_PARAMETER);
            }
            Long userId = SecurityUtil.getCurrentUserId();
			MoodHouseResponseDto moodHousesDto = houseService.getHousesByMoodAndLocation(moodTag, userId);
			return ApiResponseDto.success(SuccessCode.HOUSE_GET_SUCCESS, moodHousesDto);
		} catch (EntityNotFoundException e) {
            return ApiResponseDto.fail(ErrorCode.NOT_FOUND_HOUSE);
        } catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

    @PatchMapping("/{houseId}/pins")
    public ApiResponseDto<PinnedResponseDto> pinHouse(@PathVariable Long houseId) {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            PinnedResponseDto isPinned = houseService.togglePin(userId, houseId);
            return ApiResponseDto.success(SuccessCode.PIN_TOGGLE_SUCCESS, isPinned);
        } catch (EntityNotFoundException e) {
            return ApiResponseDto.fail(ErrorCode.NOT_FOUND_HOUSE);
        } catch (InvalidParameterException e) {
            return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
        } catch (DataAccessException e) {
            return ApiResponseDto.fail(ErrorCode.DATABASE_CONNECTION_ERROR);
        } catch (Exception e) {
            return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/pins")
    public ApiResponseDto<PinnedListResponseDto> getPinnedHouses() {
        try {
            Long userId = SecurityUtil.getCurrentUserId();
            PinnedListResponseDto pinnedListResponseDto = houseService.getPinnedHouses(userId);
            return ApiResponseDto.success(SuccessCode.PINNED_HOUSES_GET_SUCCESS, pinnedListResponseDto);
        } catch (InvalidParameterException e) {
            return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
        } catch (Exception e) {
            return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{houseId}/details/images")
    public ApiResponseDto<ImageDetailsResponseDto> getHouseImages(@PathVariable Long houseId) {
        try {
            ImageDetailsResponseDto imageDetailsResponseDto = houseService.getHouseImages(houseId);
            return ApiResponseDto.success(SuccessCode.HOUSE_DETAIL_GET_SUCCESS, imageDetailsResponseDto);
        } catch (EntityNotFoundException e) {
            return ApiResponseDto.fail(ErrorCode.NOT_FOUND_HOUSE);
        } catch (InvalidParameterException e) {
            return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
        } catch (Exception e) {
            return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{houseId}/details/rooms")
    public ApiResponseDto<RoomDetailsResponseDto> getHouseRooms(@PathVariable Long houseId) {
        try {
            RoomDetailsResponseDto roomDetailsResponseDto = houseService.getHouseRooms(houseId);
            return ApiResponseDto.success(SuccessCode.HOUSE_DETAIL_GET_SUCCESS, roomDetailsResponseDto);
        } catch (EntityNotFoundException e) {
            return ApiResponseDto.fail(ErrorCode.ROOM_NOT_FOUND);
        } catch (InvalidParameterException e) {
            return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
        } catch (Exception e) {
            return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
