package server.producer.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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
    private String roomMood;

    @Column(nullable = false)
    private String groundRule;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String locationDescription;

    @Column(nullable = false)
    private String moodTag;

    @Column(nullable = false)
    private String subMoodTag;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderPolicyType genderPolicyType;

    @Column(nullable = false)
    private String mainImgUrl;

    @Column(nullable = false)
    private String mainImgDescription;

    @Column(nullable = false)
    private String facilityImgUrl;

    @Column(nullable = false)
    private String facilityImgDescription;

    @Column(nullable = false)
    private int contractTerm;

    @Column(nullable = false)
    private String safetyLivingFacility;

    @Column(nullable = false)
    private String kitchenFacility;

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<Pin> pins = new ArrayList<>();

    @OneToMany(mappedBy = "house", cascade = CascadeType.ALL)
    private List<RecentlyViewedHouse> recentlyViewedHouses = new ArrayList<>();
}
