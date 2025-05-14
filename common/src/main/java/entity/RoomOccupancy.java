package entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "room_occupancy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomOccupancy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;  // A1, A2, A3 등

    @Column(nullable = false)
    private boolean isOccupied;  // 입주 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
} 