package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import dto.TourRequestDto;
import server.producer.common.util.SecurityUtil;
import server.producer.domain.dto.response.TourResposeDto;
import server.producer.domain.service.RoomService;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("v1/rooms")
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;

	@PostMapping("/{room_id}/tour-requests")
	public ApiResponseDto<Object> createRoomTourRequest(@PathVariable("room_id") Long roomId, @RequestBody TourRequestDto tourRequestDto){
		try {
			if (roomId == null || roomId <= 0) {
				return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER); // roomId가 유효하지 않은 경우
			}
			Long userId = SecurityUtil.getCurrentUserId();
			roomService.createRoomTourRequest(tourRequestDto);
			return ApiResponseDto.success(SuccessCode.ROOM_REQUEST_POST_SUCCESS, TourResposeDto.builder().isSuccess(true).build());
		} catch (InvalidParameterException e){
			return ApiResponseDto.fail(ErrorCode.INVALID_PARAMETER);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
