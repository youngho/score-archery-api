package to.yho.score;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import to.yho.score.domain.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class SimpleUserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRepositoryLoad() {
        assertThat(userRepository).isNotNull();
    }
}
