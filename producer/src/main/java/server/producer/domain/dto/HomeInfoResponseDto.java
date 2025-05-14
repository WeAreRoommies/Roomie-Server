@Data
@Builder
public static class RecentlyViewedHouseDto {
    private Long houseId;
    private int monthlyRent;
    private int deposit;
    private List<Integer> occupancyTypes;
    private String genderPolicy;
    private String location;
    private String locationDescription;
    private boolean isPinned;
    private String moodTag;
    private String contractTerm;
    private String mainImgUrl;
    private List<List<RoomOccupancyDto>> roomOccupancies;
}

@Data
@Builder
public static class RoomOccupancyDto {
    private String name;
    private boolean isOccupied;
} 