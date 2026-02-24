package to.yho.score.web.leaderboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Single leaderboard entry (rank, player, score, optional extra)")
@Getter
@Builder
public class LeaderboardEntryResponse {

    @Schema(description = "1-based rank")
    private int rank;

    @Schema(description = "Player display name")
    private String playerName;

    @Schema(description = "Score for this period")
    private long score;

    @Schema(description = "Avatar URL (optional)")
    private String avatar;

    @Schema(description = "Country code (optional)")
    private String country;

    @Schema(description = "Extra: level (alltime), game count (monthly), avg (weekly), best (daily)")
    private Object extra;
}
