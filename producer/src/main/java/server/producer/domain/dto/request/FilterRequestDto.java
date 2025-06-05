package server.producer.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterRequestDto {
    private String location;                 // 매물 위치
    private List<String> moodTags;          // 분위기 태그
    private Range depositRange;             // 보증금 범위
    private Range monthlyRentRange;         // 월세 범위
    private List<String> genderPolicy;      // 성별 정책
    private LocalDate preferredDate;        // 선호 입주 날짜
    private List<String> occupancyTypes;    // 숙소 유형
    private List<Integer> contractPeriod;   // 계약 기간

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
    public static class Range {
        private int min;                    // 최소 값
        private int max;                    // 최대 값
    }
}