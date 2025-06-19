package entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private String email;
    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = true)
    private String name;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = true)
    private String location = "창천동";

    @Column(nullable = true)
    private double latitude = 37.55348;

    @Column(nullable = true)
    private double longitude = 126.9381;

    @Column(nullable = true)
    private Gender gender;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private LocalDate birthDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Pin> pins = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RecentlyViewedHouse> recentlyViewedHouses = new ArrayList<>();

    // ✅ Builder를 위한 생성자 (필요한 필드만)
    @Builder
    public User(String email, String socialId, SocialType socialType, String name, String nickname, String location,
                Gender gender, String phoneNumber, LocalDate birthDate) {
        this.email = email;
        this.socialId = socialId;
        this.socialType = socialType;
        this.name = name;
        this.nickname = nickname;
        this.location = location != null ? location : "창천동";
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.pins = new ArrayList<>();
        this.recentlyViewedHouses = new ArrayList<>();
    }
}
