package to.yho.score.web.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 서버 생존 여부 확인용 API.
 * 아두이노(arduino-p1)에서 주기적으로 이 경로를 조회해 리턴값(status: "ok")으로 서버 상태를 판단할 수 있다.
 */
@Tag(name = "Health", description = "Server health check for Arduino / monitoring")
@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Operation(summary = "Health check", description = "Returns status ok when server is alive. Use from Arduino to verify API server is up.")
    @GetMapping
    public ResponseEntity<HealthResponse> health() {
        return ResponseEntity.ok(HealthResponse.ok());
    }
}
