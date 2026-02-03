package to.yho.score.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class NicknamePolicy {

    private static final int MAX_LENGTH = 50;
    private static final Pattern CLEANUP_PATTERN =
            Pattern.compile("[^0-9a-zA-Z가-힣ㄱ-ㅎㅏ-ㅣ]");
    private final NicknameProperties nicknameProperties;

    public String sanitizeNickname(String nickname) {
        if (nickname == null) {
            return "";
        }
        return nickname.trim();
    }

    public void validateNickname(String nickname) {
        if (nickname == null || nickname.isBlank()) {
            throw new RuntimeException("Nickname is required");
        }
        if (nickname.length() > MAX_LENGTH) {
            throw new RuntimeException("Nickname is too long");
        }
        if (containsProfanity(nickname)) {
            throw new RuntimeException("Nickname contains profanity");
        }
    }

    private boolean containsProfanity(String nickname) {
        String normalized = normalizeForProfanityCheck(nickname);
        for (String blocked : nicknameProperties.getBlockedWords()) {
            String normalizedBlocked = normalizeForProfanityCheck(blocked);
            if (!normalizedBlocked.isEmpty() && normalized.contains(normalizedBlocked)) {
                return true;
            }
        }
        return false;
    }

    private String normalizeForProfanityCheck(String nickname) {
        String lowerCased = nickname.toLowerCase(Locale.ROOT);
        return CLEANUP_PATTERN.matcher(lowerCased).replaceAll("");
    }
}
