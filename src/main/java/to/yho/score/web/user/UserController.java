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
    public User register(@RequestBody RegisterRequest request) {
        return userService.registerUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword());
    }
}
