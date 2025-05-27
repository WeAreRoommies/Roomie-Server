package server.producer.domain.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

public record FilterRequestDto(
		String location,                 // 매물 위치
		String moodTag,                  // 분위기 태그
		Range depositRange,              // 보증금 범위
		Range monthlyRentRange,          // 월세 범위
		List<String> genderPolicy,       // 성별 정책
		LocalDate preferredDate,         // 선호 입주 날짜
		List<String> occupancyTypes,     // 숙소 유형
		List<Integer> contractPeriod,    // 계약 기간
		@JsonProperty(defaultValue = "false")
		Boolean excludeFull              // 만실 제외 여부
) {
	public record Range(
			int min,                     // 최소 값
			int max                      // 최대 값
	) {}
}