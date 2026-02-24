package to.yho.score.domain.stage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Long> {

    boolean existsByPublicId(String publicId);

    @Query(value = "SELECT user_id AS userId, COALESCE(SUM(final_score), 0) AS totalScore, COUNT(*) AS gameCount, COALESCE(MAX(final_score), 0) AS maxScore " +
            "FROM match_history WHERE completed_at IS NOT NULL AND completed_at >= :from AND completed_at < :to " +
            "GROUP BY user_id ORDER BY totalScore DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<PeriodScoreRow> findPeriodRanking(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("offset") int offset,
            @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM (SELECT user_id FROM match_history WHERE completed_at IS NOT NULL AND completed_at >= :from AND completed_at < :to GROUP BY user_id) AS t", nativeQuery = true)
    long countPeriodRanking(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    interface PeriodScoreRow {
        Long getUserId();
        Long getTotalScore();
        Long getGameCount();
        Long getMaxScore();
    }
}
