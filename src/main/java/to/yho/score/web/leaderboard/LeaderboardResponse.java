package to.yho.score.web.leaderboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Schema(description = "Leaderboard response with entries and total count")
@Getter
@Builder
public class LeaderboardResponse {

    @Schema(description = "Ranked entries for the requested page")
    private List<LeaderboardEntryResponse> entries;

    @Schema(description = "Total number of ranked players (optional)")
    private Long total;
}
