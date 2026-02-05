package to.yho.score.domain.stage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import to.yho.score.domain.user.User;
import to.yho.score.domain.user.UserService;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StageScoreService {

    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int PUBLIC_ID_LENGTH = 22;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final UserService userService;
    private final StageRecordRepository stageRecordRepository;
    private final MatchHistoryRepository matchHistoryRepository;

    @Transactional
    public StageScoreResult recordScore(String publicId, String nickname,
                                        int worldNumber, int stageNumber, long score,
                                        Integer starsEarned, Integer playDurationSeconds, String difficultyStr) {
        User user = userService.getOrCreateUserForScore(publicId, nickname);
        Difficulty difficulty = parseDifficulty(difficultyStr);

        // Insert match_history
        String matchPublicId = generateUniqueMatchPublicId();
        MatchHistory match = MatchHistory.builder()
                .publicId(matchPublicId)
                .userId(user.getUserId())
                .worldNumber(worldNumber)
                .stageNumber(stageNumber)
                .difficulty(difficulty)
                .isCompleted(true)
                .starsEarned(starsEarned != null ? Math.min(3, Math.max(0, starsEarned)) : 0)
                .finalScore(score)
                .playDurationSeconds(playDurationSeconds)
                .completedAt(LocalDateTime.now())
                .build();
        matchHistoryRepository.save(match);

        // Upsert stage_records
        StageRecord record = stageRecordRepository
                .findByUserIdAndWorldNumberAndStageNumberAndDifficulty(
                        user.getUserId(), worldNumber, stageNumber, difficulty)
                .orElse(null);

        boolean isNewRecord = false;
        long highScore = score;

        if (record == null) {
            record = StageRecord.builder()
                    .userId(user.getUserId())
                    .worldNumber(worldNumber)
                    .stageNumber(stageNumber)
                    .difficulty(difficulty)
                    .starsEarned(starsEarned != null ? Math.min(3, Math.max(0, starsEarned)) : 0)
                    .highScore(score)
                    .bestTimeSeconds(playDurationSeconds)
                    .totalAttempts(1)
                    .totalCompletions(1)
                    .firstClearAt(LocalDateTime.now())
                    .lastPlayedAt(LocalDateTime.now())
                    .build();
            isNewRecord = true;
        } else {
            record.setTotalAttempts(record.getTotalAttempts() + 1);
            record.setTotalCompletions(record.getTotalCompletions() + 1);
            record.setLastPlayedAt(LocalDateTime.now());

            if (score > record.getHighScore()) {
                record.setHighScore(score);
                isNewRecord = true;
            }
            highScore = record.getHighScore();

            if (starsEarned != null && starsEarned > record.getStarsEarned()) {
                record.setStarsEarned(Math.min(3, starsEarned));
            }
            if (playDurationSeconds != null && (record.getBestTimeSeconds() == null
                    || playDurationSeconds < record.getBestTimeSeconds())) {
                record.setBestTimeSeconds(playDurationSeconds);
            }
        }

        stageRecordRepository.save(record);

        return new StageScoreResult(true, highScore, isNewRecord, record.getTotalCompletions());
    }

    private Difficulty parseDifficulty(String s) {
        if (s == null || s.isBlank()) return Difficulty.normal;
        try {
            return Difficulty.valueOf(s.toLowerCase());
        } catch (IllegalArgumentException e) {
            return Difficulty.normal;
        }
    }

    private String generateUniqueMatchPublicId() {
        for (int i = 0; i < 10; i++) {
            String candidate = generatePublicId();
            if (!matchHistoryRepository.existsByPublicId(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Failed to generate unique match public ID");
    }

    private String generatePublicId() {
        StringBuilder builder = new StringBuilder(PUBLIC_ID_LENGTH);
        for (int i = 0; i < PUBLIC_ID_LENGTH; i++) {
            builder.append(BASE62_CHARS.charAt(SECURE_RANDOM.nextInt(BASE62_CHARS.length())));
        }
        return builder.toString();
    }

}
