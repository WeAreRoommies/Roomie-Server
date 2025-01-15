package server.producer.entity;

import entity.House;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="pin")
@Data
public class Pin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "house_id")
    private House house;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
