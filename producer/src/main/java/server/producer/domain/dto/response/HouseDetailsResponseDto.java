package server.producer.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

public record HouseDetailsResponseDto (
    HouseInfoDto houseInfo,
    List<RoomDto> rooms
) {
    @Builder
    public HouseDetailsResponseDto{
    }
    public record HouseInfoDto (
        Long houseId,
        String name,
        String mainImgUrl,
        String monthlyRent,
        String deposit,
        String location,
        String occupancyTypes,
        String occupancyStatus,
        String genderPolicy,
        int contractTerm,
        List<String> moodTags,
        String roomMood,
        List<String> groundRule,
        boolean isPinned,
        List<String> safetyLivingFacility,
        List<String> kitchenFacility
    ) {
        @Builder
        public HouseInfoDto{
        }
    }
    public record RoomDto (
        Long roomId,
        String name,
        boolean status,
        boolean isTourAvailable,
        int occupancyType,
        String gender,
        int deposit,
        int monthlyRent,
        @JsonFormat(pattern = "yy-MM-dd")
        LocalDate contractPeriod,
        String managementFee
    ) {
        @Builder
        public RoomDto {
        }
    }
}
