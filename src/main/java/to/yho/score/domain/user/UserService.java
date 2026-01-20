package to.yho.score.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int PUBLIC_ID_LENGTH = 22;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Transactional
    public User registerUser(String nickname, String password) {
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new RuntimeException("Nickname already exists");
        }

        User user = User.builder()
                .publicId(generateUniquePublicId())
                .nickname(nickname)
                .passwordHash(password) // Simplified for now, should be hashed
                .isGuest(false)
                .accountType(AccountType.regular)
                .build();

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByPublicId(String publicId) {
        return userRepository.findByPublicId(publicId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private String generateUniquePublicId() {
        while (true) {
            String candidate = generatePublicId();
            if (!userRepository.existsByPublicId(candidate)) {
                return candidate;
            }
        }
    }

    private String generatePublicId() {
        StringBuilder builder = new StringBuilder(PUBLIC_ID_LENGTH);
        for (int i = 0; i < PUBLIC_ID_LENGTH; i++) {
            int index = SECURE_RANDOM.nextInt(BASE62_CHARS.length());
            builder.append(BASE62_CHARS.charAt(index));
        }
        return builder.toString();
    }
}
