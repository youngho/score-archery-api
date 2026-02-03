package to.yho.score.web.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Admin", description = "Admin monitoring APIs")
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "List active users", description = "Returns users who currently have an active session (in-game or recently active)")
    @GetMapping("/active-users")
    public List<ActiveUserResponse> getActiveUsers() {
        return adminService.getActiveUsers();
    }
}
