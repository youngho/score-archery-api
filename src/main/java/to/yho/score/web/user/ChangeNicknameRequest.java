package to.yho.score.web.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeNicknameRequest {
    @Schema(description = "New nickname", example = "new_nickname")
    private String nickname;
}
