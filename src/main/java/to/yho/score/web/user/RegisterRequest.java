package to.yho.score.web.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @Schema(description = "User's unique username", example = "john_doe")
    private String username;

    @Schema(description = "User's email address", example = "john@example.com")
    private String email;

    @Schema(description = "User's password", example = "password123")
    private String password;
}
