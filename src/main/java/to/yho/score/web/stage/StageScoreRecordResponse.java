package to.yho.score.web.stage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Response after recording stage score")
@Getter
@Builder
public class StageScoreRecordResponse {

    @Schema(description = "Whether the record was saved successfully")
    private boolean success;

    @Schema(description = "High score for this stage (updated or existing)")
    private Long highScore;

    @Schema(description = "Whether this play achieved a new high score")
    private boolean isNewRecord;

    @Schema(description = "Total completions for this stage")
    private Integer totalCompletions;
}
