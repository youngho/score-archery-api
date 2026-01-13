package to.yho.score;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import to.yho.score.domain.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SimpleUserTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRepositoryLoad() {
        assertThat(userRepository).isNotNull();
    }
}
