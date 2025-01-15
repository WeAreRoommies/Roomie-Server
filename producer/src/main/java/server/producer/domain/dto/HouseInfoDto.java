package server.producer.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseInfoDto {
    private Long houseId;
    private String name;
    private String mainImgUrl;
    private String monthlyRent;
    private String deposit;
    private String location;
    private String occupancyTypes;
    private String occupancyStatus;
    private String genderPolicy;
    private int contractTerm;
    private List<String> moodTags;
    private String roomMood;
    private List<String> groundRule;
    private int maintenanceCost;
    private boolean isPinned;
    private List<String> safetyLivingFacility;
    private List<String> kitchenFacility;
}
