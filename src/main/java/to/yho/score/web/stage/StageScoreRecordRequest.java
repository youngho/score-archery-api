package to.yho.score.web.stage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Request to record stage score")
@Getter
@Setter
@NoArgsConstructor
public class StageScoreRecordRequest {

    @Schema(description = "User public ID (from Unity client)", required = true)
    private String publicId;

    @Schema(description = "Optional nickname (used when creating guest user)", example = "Player")
    private String nickname;

    @Schema(description = "World number (1-based)", required = true, example = "1")
    private Integer worldNumber;

    @Schema(description = "Stage number (1-based)", required = true, example = "4")
    private Integer stageNumber;

    @Schema(description = "Final score achieved", required = true, example = "15")
    private Long score;

    @Schema(description = "Stars earned (0-3)", example = "2")
    private Integer starsEarned;

    @Schema(description = "Play duration in seconds", example = "90")
    private Integer playDurationSeconds;

    @Schema(description = "Difficulty level", example = "normal")
    private String difficulty;
}
