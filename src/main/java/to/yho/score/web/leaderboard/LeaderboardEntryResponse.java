package to.yho.score.web.leaderboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Single leaderboard entry (rank, player, score, match stats)")
@Getter
@Builder
public class LeaderboardEntryResponse {

    @Schema(description = "1-based rank")
    private int rank;

    @Schema(description = "Player display name")
    private String playerName;

    @Schema(description = "Ranking score (alltime: sum of stage high scores; period: sum of match final_score in window)")
    private long score;

    @Schema(description = "Avatar URL (optional)")
    private String avatar;

    @Schema(description = "Country code (optional)")
    private String country;

    @Schema(description = "Extra: level (alltime), game count (monthly), unused for weekly (use avgScorePerMatch), best single-game score (daily)")
    private Object extra;

    @Schema(description = "Matches played in the period (monthly/weekly/daily); omitted for alltime")
    private Long gameCount;

    @Schema(description = "Average final_score per match in the period; for alltime: AVG(final_score) over all completed matches")
    private Double avgScorePerMatch;

    @Schema(description = "Average match accuracy (0–100), null if no data")
    private Double avgAccuracy;

    @Schema(description = "Average total_arrows_used per match, null if no data")
    private Double avgArrowsPerMatch;

    @Schema(description = "stage_scene_name of the most recently completed match in scope")
    private String lastStageSceneName;
}
