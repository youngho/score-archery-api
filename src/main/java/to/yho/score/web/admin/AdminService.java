package to.yho.score.web.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final JdbcTemplate jdbcTemplate;

    /**
     * Returns users who have an active session (is_active = 1 and expires_at > now).
     * Ordered by last_activity_at descending.
     */
    public List<ActiveUserResponse> getActiveUsers() {
        String sql = """
            SELECT u.public_id, u.nickname, s.last_activity_at, s.device_type, s.session_id
            FROM user_sessions s
            JOIN users u ON u.user_id = s.user_id
            WHERE s.is_active = 1 AND s.expires_at > NOW()
            ORDER BY s.last_activity_at DESC
            """;
        return jdbcTemplate.query(sql, this::mapRow);
    }

    private ActiveUserResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
        return ActiveUserResponse.builder()
                .publicId(rs.getString("public_id"))
                .nickname(rs.getString("nickname"))
                .lastActivityAt(rs.getObject("last_activity_at", LocalDateTime.class))
                .deviceType(rs.getString("device_type"))
                .sessionId(rs.getString("session_id"))
                .build();
    }
}
