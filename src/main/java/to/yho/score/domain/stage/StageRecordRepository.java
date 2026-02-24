package to.yho.score.domain.stage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StageRecordRepository extends JpaRepository<StageRecord, Long> {

    Optional<StageRecord> findByUserIdAndWorldNumberAndStageNumberAndDifficulty(
            Long userId, Integer worldNumber, Integer stageNumber, Difficulty difficulty);

    @Query(value = "SELECT user_id AS userId, SUM(high_score) AS totalScore FROM stage_records GROUP BY user_id ORDER BY totalScore DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<LeaderboardScoreRow> findAlltimeRanking(@Param("offset") int offset, @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) FROM (SELECT user_id FROM stage_records GROUP BY user_id) AS t", nativeQuery = true)
    long countAlltimeRanking();

    interface LeaderboardScoreRow {
        Long getUserId();
        Long getTotalScore();
    }
}
