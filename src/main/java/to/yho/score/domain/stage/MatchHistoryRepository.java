package to.yho.score.domain.stage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Long> {

    boolean existsByPublicId(String publicId);

    @Query(value = "SELECT base.user_id AS userId, base.totalScore AS totalScore, base.gameCount AS gameCount, base.maxScore AS maxScore, " +
            "base.avgAccuracy AS avgAccuracy, base.avgArrowsPerMatch AS avgArrowsPerMatch, " +
            "(SELECT mh2.stage_scene_name FROM match_history mh2 WHERE mh2.user_id = base.user_id AND mh2.completed_at IS NOT NULL " +
            "AND mh2.completed_at >= :from AND mh2.completed_at < :to ORDER BY mh2.completed_at DESC, mh2.match_id DESC LIMIT 1) AS lastStageSceneName " +
            "FROM ( " +
            "  SELECT user_id, COALESCE(SUM(final_score), 0) AS totalScore, COUNT(*) AS gameCount, COALESCE(MAX(final_score), 0) AS maxScore, " +
            "  AVG(accuracy) AS avgAccuracy, AVG(total_arrows_used) AS avgArrowsPerMatch " +
            "  FROM match_history WHERE completed_at IS NOT NULL AND completed_at >= :from AND completed_at < :to " +
            "  GROUP BY user_id " +
            ") base ORDER BY base.totalScore DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PeriodScoreRow> findPeriodRanking(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM (SELECT user_id FROM match_history WHERE completed_at IS NOT NULL AND completed_at >= :from AND completed_at < :to GROUP BY user_id) AS t", nativeQuery = true)
    long countPeriodRanking(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query(value = "SELECT user_id AS userId, AVG(accuracy) AS avgAccuracy, AVG(total_arrows_used) AS avgArrowsPerMatch, AVG(final_score) AS avgMatchScore " +
            "FROM match_history WHERE completed_at IS NOT NULL AND user_id IN (:userIds) GROUP BY user_id", nativeQuery = true)
    List<UserMatchAggregateRow> findAggregatesForUsers(@Param("userIds") Collection<Long> userIds);

    @Query(value = "SELECT user_id AS userId, stage_scene_name AS lastStageSceneName FROM ( " +
            "SELECT user_id, stage_scene_name, ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY completed_at DESC, match_id DESC) AS rn " +
            "FROM match_history WHERE completed_at IS NOT NULL AND user_id IN (:userIds) " +
            ") t WHERE rn = 1", nativeQuery = true)
    List<UserLastStageRow> findLastStageSceneNamesForUsers(@Param("userIds") Collection<Long> userIds);

    interface PeriodScoreRow {
        Long getUserId();
        Long getTotalScore();
        Long getGameCount();
        Long getMaxScore();
        Double getAvgAccuracy();
        Double getAvgArrowsPerMatch();
        String getLastStageSceneName();
    }

    interface UserMatchAggregateRow {
        Long getUserId();
        Double getAvgAccuracy();
        Double getAvgArrowsPerMatch();
        Double getAvgMatchScore();
    }

    interface UserLastStageRow {
        Long getUserId();
        String getLastStageSceneName();
    }
}
