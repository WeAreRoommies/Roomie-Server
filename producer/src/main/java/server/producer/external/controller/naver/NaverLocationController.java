package server.producer.external.controller.naver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.producer.common.dto.ApiResponseDto;
import server.producer.common.dto.enums.SuccessCode;
import server.producer.external.controller.naver.dto.LocationsDto;
import server.producer.external.service.naver.NaverService;


@RestController
@RequiredArgsConstructor
@Slf4j
public class NaverLocationController {

    private final NaverService naverService;

    @GetMapping("/v1/locations")
    public ApiResponseDto<LocationsDto> getLocations(
            @RequestParam(name = "q") final String query
    ) {
        log.info("Get locations from Naver");
        log.info("query: {}", query);
        log.info("naverService: {}", naverService);
        System.out.println("logtest");
        return ApiResponseDto.success(SuccessCode.HOUSE_GET_SUCCESS, naverService.getLocations(query));
    }
}
