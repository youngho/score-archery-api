package to.yho.score.domain.stage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StageRecordRepository extends JpaRepository<StageRecord, Long> {

    Optional<StageRecord> findByUserIdAndWorldNumberAndStageNumberAndDifficulty(
            Long userId, Integer worldNumber, Integer stageNumber, Difficulty difficulty);
}
