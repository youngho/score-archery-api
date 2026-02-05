package to.yho.score.domain.stage;

/**
 * Result of recording a stage score.
 */
public record StageScoreResult(
        boolean success,
        long highScore,
        boolean isNewRecord,
        int totalCompletions
) {}
