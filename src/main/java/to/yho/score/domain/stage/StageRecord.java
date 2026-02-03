package to.yho.score.domain.stage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Stage record entity - best record per user per stage")
@Entity
@Table(name = "stage_records", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "world_number", "stage_number", "difficulty"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "world_number", nullable = false)
    private Integer worldNumber;

    @Column(name = "stage_number", nullable = false)
    private Integer stageNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Difficulty difficulty = Difficulty.normal;

    @Column(name = "stars_earned")
    @Builder.Default
    private Integer starsEarned = 0;

    @Column(name = "high_score")
    @Builder.Default
    private Long highScore = 0L;

    @Column(name = "best_time_seconds")
    private Integer bestTimeSeconds;

    @Column(name = "best_accuracy", precision = 5, scale = 2)
    private BigDecimal bestAccuracy;

    @Column(name = "best_combo")
    @Builder.Default
    private Integer bestCombo = 0;

    @Column(name = "total_attempts")
    @Builder.Default
    private Integer totalAttempts = 0;

    @Column(name = "total_completions")
    @Builder.Default
    private Integer totalCompletions = 0;

    @Column(name = "first_clear_at")
    private LocalDateTime firstClearAt;

    @Column(name = "last_played_at")
    private LocalDateTime lastPlayedAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
