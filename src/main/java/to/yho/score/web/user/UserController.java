package to.yho.score.web.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import to.yho.score.domain.user.User;
import to.yho.score.domain.user.UserService;

@Tag(name = "User", description = "User management APIs")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register a new user", description = "Creates a new user account with basic profile information")
    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.getNickname(),
                request.getPassword());
        return UserResponse.from(user);
    }

    @Operation(summary = "Get user by public ID", description = "Fetches a user using public_id (safe for external exposure)")
    @GetMapping("/{publicId}")
    public UserResponse getByPublicId(@PathVariable String publicId) {
        return UserResponse.from(userService.getUserByPublicId(publicId));
    }

    @Operation(summary = "Change nickname", description = "Updates user's nickname with duplicate and profanity checks")
    @PatchMapping("/{publicId}/nickname")
    public UserResponse changeNickname(@PathVariable String publicId,
                                       @RequestBody ChangeNicknameRequest request) {
        User user = userService.changeNickname(publicId, request.getNickname());
        return UserResponse.from(user);
    }
}
