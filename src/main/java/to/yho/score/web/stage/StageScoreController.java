package to.yho.score.web.stage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import to.yho.score.domain.stage.StageScoreResult;
import to.yho.score.domain.stage.StageScoreService;

@Tag(name = "Stage Score", description = "Stage score recording APIs")
@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageScoreController {

    private final StageScoreService stageScoreService;

    @Operation(summary = "Record stage score", description = "Records user's score when a stage is completed")
    @PostMapping("/record")
    public ResponseEntity<StageScoreRecordResponse> recordScore(@RequestBody StageScoreRecordRequest request) {
        if (request.getPublicId() == null || request.getPublicId().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(StageScoreRecordResponse.builder()
                            .success(false)
                            .highScore(0L)
                            .isNewRecord(false)
                            .totalCompletions(0)
                            .build());
        }
        if (request.getWorldNumber() == null || request.getStageNumber() == null || request.getScore() == null) {
            return ResponseEntity.badRequest()
                    .body(StageScoreRecordResponse.builder()
                            .success(false)
                            .highScore(0L)
                            .isNewRecord(false)
                            .totalCompletions(0)
                            .build());
        }

        StageScoreResult result = stageScoreService.recordScore(
                request.getPublicId(),
                request.getNickname(),
                request.getWorldNumber(),
                request.getStageNumber(),
                request.getScore(),
                request.getStarsEarned(),
                request.getPlayDurationSeconds(),
                request.getDifficulty()
        );

        return ResponseEntity.ok(StageScoreRecordResponse.builder()
                .success(result.success())
                .highScore(result.highScore())
                .isNewRecord(result.isNewRecord())
                .totalCompletions(result.totalCompletions())
                .build());
    }
}
