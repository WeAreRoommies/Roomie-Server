package entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="room")
@Data
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name="";

    @Column(nullable = false)
    private int monthlyRent;

    @Column(nullable = false)
    private int deposit;

    @Column(nullable = false)
    private String facility="";

    @Column(nullable = true)
    private LocalDate contractPeriod;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderType gender;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private int occupancyType;

    @Column(nullable = false)
    private String mainImgUrl="";

    @Column(nullable = false)
    private String managementFee="";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<HousingRequest> housingRequests = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomOccupancy> roomOccupancies = new ArrayList<>();

    public boolean isTourAvailable(){
        if (this.contractPeriod == null) return true;
        if (this.contractPeriod.isAfter(LocalDate.now().plusDays(60))) return false;
        return true;
    }

    public int getCurrentOccupancyCount() {
        return (int) roomOccupancies.stream()
                .filter(RoomOccupancy::isOccupied)
                .count();
    }
}
