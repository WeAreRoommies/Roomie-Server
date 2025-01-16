package dto;
import java.time.LocalDate;
import lombok.Builder;

public record TourRequestDto(
		String name,
		LocalDate birth,
		String gender,
		String phoneNumber,
		LocalDate preferredDate,
		String message,
		Long roomId,
		Long houseId
) {
	@Builder
	public TourRequestDto {
	}
}