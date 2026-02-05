package to.yho.score.domain.stage;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Match history - each play session record")
@Entity
@Table(name = "match_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "match_id")
    private Long matchId;

    @Column(name = "public_id", nullable = false, unique = true, length = 22)
    private String publicId;

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

    @Column(name = "is_completed")
    @Builder.Default
    private Boolean isCompleted = true;

    @Column(name = "stars_earned", columnDefinition = "TINYINT")
    @Builder.Default
    private Integer starsEarned = 0;

    @Column(name = "final_score")
    @Builder.Default
    private Long finalScore = 0L;

    @Column(name = "total_arrows_used")
    private Integer totalArrowsUsed;

    @Column(name = "arrows_hit")
    private Integer arrowsHit;

    @Column(name = "arrows_missed")
    private Integer arrowsMissed;

    @Column(name = "accuracy", precision = 5, scale = 2)
    private BigDecimal accuracy;

    @Column(name = "bullseyes")
    private Integer bullseyes;

    @Column(name = "perfects")
    private Integer perfects;

    @Column(name = "max_combo")
    private Integer maxCombo;

    @Column(name = "play_duration_seconds")
    private Integer playDurationSeconds;

    @CreationTimestamp
    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
