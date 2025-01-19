package server.producer.domain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import dto.TourRequestDto;
import server.producer.domain.service.RoomService;

@RestController("v1/rooms")
@RequiredArgsConstructor
public class RoomController {
	private final RoomService roomService;

	@PostMapping("/{room_id}/tour-requests")
	public ApiResponseDto<Object> createRoomTourRequest(@PathVariable("room_id") Long roomId, @RequestBody TourRequestDto tourRequestDto){
		try {
			roomService.createRoomTourRequest(tourRequestDto);
			return ApiResponseDto.success(SuccessCode.ROOM_REQUEST_POST_SUCCESS);
		} catch (Exception e) {
			return ApiResponseDto.fail(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}
