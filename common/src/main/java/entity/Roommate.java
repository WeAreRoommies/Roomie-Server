package entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="roommate")
@Data
public class Roommate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String age;
    private String job;
    private String mbti;
    private String sleepTime;
    private String activateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;
}
