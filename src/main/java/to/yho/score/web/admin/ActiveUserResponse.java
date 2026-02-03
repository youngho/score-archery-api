package to.yho.score.web.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "Currently active (in-session) user info for admin monitoring")
@Getter
@Builder
public class ActiveUserResponse {

    @Schema(description = "User public ID", example = "b8Zk2Qp1fZ0qW3mN7aLx9B")
    private String publicId;

    @Schema(description = "User nickname", example = "john_doe")
    private String nickname;

    @Schema(description = "Last activity timestamp for this session")
    private LocalDateTime lastActivityAt;

    @Schema(description = "Device type", example = "web", allowableValues = {"web", "ios", "android"})
    private String deviceType;

    @Schema(description = "Session ID")
    private String sessionId;
}
