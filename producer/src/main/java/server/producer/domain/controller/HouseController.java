package server.producer.domain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.ErrorCode;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.domain.dto.response.HomeInfoResponseDto;
import server.producer.domain.dto.response.HouseDetailsResponseDto;
import server.producer.domain.service.HouseService;

@RestController
@RequestMapping("/v1/houses")
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

}
