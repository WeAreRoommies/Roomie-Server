package server.producer.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoomDto {
    private Long roomId;
    private String name;
    private boolean status;
    private int occupancyType;
    private String gender;
    private int deposit;
    private int prepaidUtilities;
    private int monthlyRent;
    private String contractPeriod;
    private String managementFee;
}