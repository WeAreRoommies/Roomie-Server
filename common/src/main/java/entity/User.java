package entity;

import jakarta.persistence.*;
import lombok.*;

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

    private String email;
    private String socialId;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Pin> pins = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RecentlyViewedHouse> recentlyViewedHouses = new ArrayList<>();

    // ✅ Builder를 위한 생성자 (필요한 필드만)
    @Builder
    public User(String email, String socialId, SocialType socialType, String name, String location) {
        this.email = email;
        this.socialId = socialId;
        this.socialType = socialType;
        this.name = name;
        this.location = location;
        this.pins = new ArrayList<>();
        this.recentlyViewedHouses = new ArrayList<>();
    }
}