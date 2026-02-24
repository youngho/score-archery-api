package to.yho.score.web.leaderboard;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import to.yho.score.domain.leaderboard.LeaderboardService;

@Tag(name = "Leaderboard", description = "Global leaderboard by period")
@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @Operation(summary = "Get leaderboard", description = "Returns ranked entries for the given period. period: alltime | monthly | weekly | daily. offset 1-based, limit 1–100.")
    @GetMapping
    public LeaderboardResponse getLeaderboard(
            @Parameter(description = "alltime | monthly | weekly | daily") @RequestParam(defaultValue = "alltime") String period,
            @Parameter(description = "1-based start rank") @RequestParam(defaultValue = "1") int offset,
            @Parameter(description = "Page size (max 100)") @RequestParam(defaultValue = "50") int limit) {
        return leaderboardService.getLeaderboard(period, offset, limit);
    }
}
