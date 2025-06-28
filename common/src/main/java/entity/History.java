package entity;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "history")
@Data
public class History {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long historyId;

	@Column(name = "body", nullable = false, length = 500)
	private String body;

	@CreatedDate
	private LocalDateTime createdAt;
}