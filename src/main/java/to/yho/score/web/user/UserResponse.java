package to.yho.score.web.user;

import lombok.Builder;
import lombok.Getter;
import to.yho.score.domain.user.AccountType;
import to.yho.score.domain.user.User;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private String publicId;
    private String nickname;
    private String avatarUrl;
    private Integer level;
    private Long experiencePoints;
    private Long coins;
    private Long gems;
    private AccountType accountType;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .publicId(user.getPublicId())
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .level(user.getLevel())
                .experiencePoints(user.getExperiencePoints())
                .coins(user.getCoins())
                .gems(user.getGems())
                .accountType(user.getAccountType())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
