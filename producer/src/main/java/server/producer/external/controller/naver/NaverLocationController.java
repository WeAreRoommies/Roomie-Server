package server.producer.external.controller.naver;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.producer.external.controller.naver.dto.LocationsDto;
import server.producer.external.service.naver.NaverService;


@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
public class NaverLocationController {

    private final NaverService naverService;

    @GetMapping
    public ResponseEntity<LocationsDto> getLocations(
            @RequestParam(name = "q") final String query
    ) {
        return ResponseEntity.ok(naverService.getLocations(query));
    }
}
