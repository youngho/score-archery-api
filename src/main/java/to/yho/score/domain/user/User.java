package to.yho.score.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Schema(description = "User entity")
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Schema(description = "User unique identifier", example = "1")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Schema(description = "Public identifier exposed to clients", example = "b8Zk2Qp1fZ0qW3mN7aLx9B")
    @Column(name = "public_id", nullable = false, unique = true, length = 22)
    private String publicId;

    @Schema(description = "User's nickname", example = "john_doe")
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(unique = true)
    private String email;

    private String passwordHash;

    private String avatarUrl;

    private Integer level;

    private Long experiencePoints;

    private Long coins;

    private Long gems;

    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    private Boolean isGuest;

    @Column(unique = true)
    private String appleId;

    @Column(unique = true)
    private String facebookId;

    @Column(unique = true)
    private String googleId;

    private Boolean isActive;

    private Boolean isBanned;

    private String banReason;

    private LocalDateTime banUntil;

    private Integer totalPlayTime;

    private LocalDateTime lastLoginAt;

    private Integer loginCount;

    @Column(columnDefinition = "json")
    private String settings;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @PrePersist
    public void prePersist() {
        if (this.level == null)
            this.level = 1;
        if (this.experiencePoints == null)
            this.experiencePoints = 0L;
        if (this.coins == null)
            this.coins = 0L;
        if (this.gems == null)
            this.gems = 0L;
        if (this.accountType == null)
            this.accountType = AccountType.guest;
        if (this.isGuest == null)
            this.isGuest = true;
        if (this.isActive == null)
            this.isActive = true;
        if (this.isBanned == null)
            this.isBanned = false;
        if (this.totalPlayTime == null)
            this.totalPlayTime = 0;
        if (this.loginCount == null)
            this.loginCount = 0;
    }
}
