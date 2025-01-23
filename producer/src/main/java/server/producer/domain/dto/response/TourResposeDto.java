package server.producer.domain.dto.response;

import lombok.Builder;

public record TourResposeDto (
		boolean isSuccess
) {
	@Builder
	public TourResposeDto {
	}
}