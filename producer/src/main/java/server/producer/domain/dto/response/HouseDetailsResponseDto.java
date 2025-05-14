package server.producer.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

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
        int maintenanceCost,
        boolean isPinned,
        List<String> safetyLivingFacility,
        List<String> kitchenFacility
    ) {
        @Builder
        public HouseInfoDto{
        }
    }
    @Data
    @Builder
    public static class RoomDto {
        private Long roomId;
        private String name;
        private int status;  // 현재 입주자 수
        private boolean isTourAvailable;
        private int occupancyType;
        private String gender;
        private int deposit;
        private int monthlyRent;
        private LocalDate contractPeriod;
        private String managementFee;
        private List<RoomOccupancyDto> roomOccupancies;
    }

    @Data
    @Builder
    public static class RoomOccupancyDto {
        private String name;
        private boolean isOccupied;
    }
}
