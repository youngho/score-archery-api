package to.yho.score.domain.session;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import to.yho.score.domain.user.UserRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Creates or refreshes user_sessions so that active-users reflects current API usage.
 * Called when a user is identified (e.g. by publicId) on key endpoints.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private static final int SESSION_EXPIRY_DAYS = 30;

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    /**
     * Records or refreshes an active session for the user identified by publicId.
     * Safe to call with invalid publicId (no-op). Device type defaults to "web" if null.
     */
    @Transactional
    public void createOrRefreshSession(String publicId, String deviceType, String deviceId, String ipAddress) {
        if (publicId == null || publicId.isBlank()) {
            return;
        }
        Optional<Long> userIdOpt = userRepository.findByPublicId(publicId).map(u -> u.getUserId());
        if (userIdOpt.isEmpty()) {
            return;
        }
        long userId = userIdOpt.get();
        String device = deviceType != null && !deviceType.isBlank() ? deviceType : "web";

        int updated = jdbcTemplate.update("""
                UPDATE user_sessions
                SET last_activity_at = CURRENT_TIMESTAMP, expires_at = TIMESTAMPADD(SQL_TSI_DAY, ?, CURRENT_TIMESTAMP)
                WHERE user_id = ? AND is_active = 1 AND expires_at > CURRENT_TIMESTAMP
                """, SESSION_EXPIRY_DAYS, userId);

        if (updated == 0) {
            String sessionId = UUID.randomUUID().toString();
            String placeholderToken = "api:" + UUID.randomUUID().toString();
            jdbcTemplate.update("""
                    INSERT INTO user_sessions (session_id, user_id, access_token, device_type, device_id, ip_address, is_active, last_activity_at, expires_at)
                    VALUES (?, ?, ?, ?, ?, ?, 1, CURRENT_TIMESTAMP, TIMESTAMPADD(SQL_TSI_DAY, ?, CURRENT_TIMESTAMP))
                    """, sessionId, userId, placeholderToken, device, deviceId, ipAddress, SESSION_EXPIRY_DAYS);
        }
    }
}
