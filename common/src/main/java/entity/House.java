package entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name="house")
@Data
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String roomMood = "";

    @Column(nullable = false)
    private String groundRule = "";

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String locationDescription = "";

    @Column(nullable = false)
    private String moodTag;

    @Column(nullable = false)
    private String subMoodTag = "";

    @Column(nullable = false, name = "gender_policy")
    @Enumerated(EnumType.STRING)
    private GenderPolicyType genderPolicy;

    @Column(nullable = false)
    private String mainImgUrl;

    @Column(nullable = false)
    private String mainImgDescription = "";

    @Column(nullable = false)
    private String facilityImgUrl = "";

    @Column(nullable = false)
    private String facilityImgDescription = "";

    @Column(nullable = false)
    private String floorImgUrl = "";

    @Column(nullable = false)
    private int contractTerm;

    @Column(nullable = false)
    private String safetyLivingFacility = "";

    @Column(nullable = false)
    private String kitchenFacility = "";

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<Pin> pins = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<RecentlyViewedHouse> recentlyViewedHouses = new ArrayList<>();

    @Column
    private int label = 0;

    public String calculateMonthlyRent() {
        int minMonthlyRent = rooms.stream()
                .mapToInt(Room::getMonthlyRent)
                .min()
                .orElse(10000000); // 기본값

        int maxMonthlyRent = rooms.stream()
                .mapToInt(Room::getMonthlyRent)
                .max()
                .orElse(0); // 기본값
        return (minMonthlyRent/10000) + "~" + (maxMonthlyRent/10000);
    }

    public String calculateDeposit() {
        int minDeposit = rooms.stream()
                .mapToInt(Room::getDeposit)
                .min()
                .orElse(10000000); // 기본값

        int maxDeposit = rooms.stream()
                .mapToInt(Room::getDeposit)
                .max()
                .orElse(0); // 기본값
        return (minDeposit/10000) + "~" + (maxDeposit/10000);
    }

    public String calculateOccupancyType() {
        List<Integer> occupancyTypes = rooms.stream()
                .map(Room::getOccupancyType)
                .distinct()
                .sorted()
                .toList();
        return occupancyTypes.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")) + "인실";
    }

    public String calculateOccupancyStatus() {
        int max = 0;
        int current = 0;
        for (Room room : this.rooms) {
            current += room.getStatus();
            max += room.getOccupancyType();
        }
        return current + "/" + max;
    }

    public List<String> mergeTags() {
        String moodTag = this.getMoodTag();
        String subTag = this.getSubMoodTag();
        List<String> tags = Arrays.stream((moodTag+" "+ subTag).split(" ")).toList();
        return tags;
    }
}
