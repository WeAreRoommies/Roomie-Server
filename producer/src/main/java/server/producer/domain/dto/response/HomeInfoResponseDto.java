package server.producer.domain.dto.response;

import lombok.Builder;

import java.util.List;

public record HomeInfoResponseDto(
		String nickname,
		String location,
		List<RecentlyViewedHouseDto> recentlyViewedHouses
) {
	@Builder
	public HomeInfoResponseDto {
	}

	public record RecentlyViewedHouseDto(
			Long houseId,
			String monthlyRent,
			String deposit,
			String occupancyTypes,
			String location,
			String genderPolicy,
			String locationDescription,
			boolean isPinned,
			String moodTag,
			int contractTerm,
			String mainImgUrl
	) {
		@Builder
		public RecentlyViewedHouseDto {
		}
	}
}