package server.producer.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RoommateDto {
    private String name;
    private int age;
    private String job;
    private String mbti;
    private String sleepTime;
    private String activityTime;
}
