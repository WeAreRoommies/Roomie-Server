package entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="housing_request")
@Data
public class HousingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDate birth;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private LocalDate preferredDate;

    @Column(nullable = true)
    private String message;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    public HousingRequest(String name, LocalDate birth, String gender, String phoneNumber, LocalDate preferredDate, String message, Room room) {
        this.name = name;
        this.birth = birth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.preferredDate = preferredDate;
        this.message = message;
        this.room = room;
    }

    public HousingRequest() {

    }
}
