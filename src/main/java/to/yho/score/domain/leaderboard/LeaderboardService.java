package to.yho.score.domain.leaderboard;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import to.yho.score.domain.stage.MatchHistoryRepository;
import to.yho.score.domain.stage.StageRecordRepository;
import to.yho.score.domain.user.User;
import to.yho.score.domain.user.UserRepository;
import to.yho.score.web.leaderboard.LeaderboardEntryResponse;
import to.yho.score.web.leaderboard.LeaderboardResponse;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final StageRecordRepository stageRecordRepository;
    private final MatchHistoryRepository matchHistoryRepository;
    private final UserRepository userRepository;

    public LeaderboardResponse getLeaderboard(String period, int offset, int limit) {
        if (offset < 1) offset = 1;
        if (limit < 1 || limit > 100) limit = 50;
        int zeroBasedOffset = offset - 1;

        return switch (period != null ? period.toLowerCase() : "alltime") {
            case "monthly" -> getPeriodLeaderboard(zeroBasedOffset, limit, getMonthlyRange(), "monthly");
            case "weekly" -> getPeriodLeaderboard(zeroBasedOffset, limit, getWeeklyRange(), "weekly");
            case "daily" -> getPeriodLeaderboard(zeroBasedOffset, limit, getDailyRange(), "daily");
            default -> getAlltimeLeaderboard(zeroBasedOffset, limit);
        };
    }

    private LeaderboardResponse getAlltimeLeaderboard(int offset, int limit) {
        long total = stageRecordRepository.countAlltimeRanking();
        List<StageRecordRepository.LeaderboardScoreRow> rows = stageRecordRepository.findAlltimeRanking(offset, limit);
        List<Long> userIds = rows.stream().map(StageRecordRepository.LeaderboardScoreRow::getUserId).toList();
        Map<Long, User> users = userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getUserId, u -> u));

        List<LeaderboardEntryResponse> entries = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            StageRecordRepository.LeaderboardScoreRow row = rows.get(i);
            User user = users.get(row.getUserId());
            entries.add(LeaderboardEntryResponse.builder()
                    .rank(offset + i + 1)
                    .playerName(user != null ? user.getNickname() : "?")
                    .score(row.getTotalScore() != null ? row.getTotalScore() : 0L)
                    .avatar(user != null ? user.getAvatarUrl() : null)
                    .country(null)
                    .extra(user != null ? user.getLevel() : null)
                    .build());
        }
        return LeaderboardResponse.builder().entries(entries).total(total).build();
    }

    private record PeriodRange(LocalDateTime from, LocalDateTime to) {}

    private PeriodRange getMonthlyRange() {
        YearMonth ym = YearMonth.now();
        return new PeriodRange(
                ym.atDay(1).atStartOfDay(),
                ym.plusMonths(1).atDay(1).atStartOfDay());
    }

    private PeriodRange getWeeklyRange() {
        LocalDate monday = LocalDate.now().with(DayOfWeek.MONDAY);
        return new PeriodRange(
                monday.atStartOfDay(),
                monday.plusWeeks(1).atStartOfDay());
    }

    private PeriodRange getDailyRange() {
        LocalDate today = LocalDate.now();
        return new PeriodRange(
                today.atStartOfDay(),
                today.plusDays(1).atStartOfDay());
    }

    private LeaderboardResponse getPeriodLeaderboard(int offset, int limit, PeriodRange range, String periodKind) {
        long total = matchHistoryRepository.countPeriodRanking(range.from(), range.to());
        List<MatchHistoryRepository.PeriodScoreRow> rows = matchHistoryRepository.findPeriodRanking(
                range.from(), range.to(), offset, limit);
        List<Long> userIds = rows.stream().map(MatchHistoryRepository.PeriodScoreRow::getUserId).toList();
        Map<Long, User> users = userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getUserId, u -> u));

        List<LeaderboardEntryResponse> entries = new ArrayList<>();
        for (int i = 0; i < rows.size(); i++) {
            MatchHistoryRepository.PeriodScoreRow row = rows.get(i);
            User user = users.get(row.getUserId());
            long totalScore = row.getTotalScore() != null ? row.getTotalScore() : 0L;
            long gameCount = row.getGameCount() != null ? row.getGameCount() : 0L;
            long maxScore = row.getMaxScore() != null ? row.getMaxScore() : 0L;

            Object extra = switch (periodKind) {
                case "monthly" -> gameCount;
                case "weekly" -> (gameCount > 0) ? (totalScore / gameCount) : 0L;
                case "daily" -> maxScore;
                default -> null;
            };

            entries.add(LeaderboardEntryResponse.builder()
                    .rank(offset + i + 1)
                    .playerName(user != null ? user.getNickname() : "?")
                    .score(totalScore)
                    .avatar(user != null ? user.getAvatarUrl() : null)
                    .country(null)
                    .extra(extra)
                    .build());
        }
        return LeaderboardResponse.builder().entries(entries).total(total).build();
    }
}
