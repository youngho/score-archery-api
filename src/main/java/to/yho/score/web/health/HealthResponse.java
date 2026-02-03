package to.yho.score.web.health;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 헬스 체크 API 응답.
 * 아두이노(arduino-p1) 등에서 API 서버 생존 여부 확인 시 사용.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthResponse {

    public static final String STATUS_OK = "ok";

    @JsonProperty("status")
    private String status;

    public static HealthResponse ok() {
        return new HealthResponse(STATUS_OK);
    }
}
