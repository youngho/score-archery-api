package to.yho.score;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import to.yho.score.domain.session.SessionService;
import to.yho.score.domain.user.UserService;
import to.yho.score.web.user.UserController;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UserRegistrationStandaloneTest {

    @Autowired
    private UserService userService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        SessionService sessionService = Mockito.mock(SessionService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService, sessionService)).build();
    }

    @Test
    void registerUserSuccessfully() throws Exception {
        String nickname = "testuser_" + System.currentTimeMillis();
        String publicId = UUID.randomUUID().toString().replace("-", "").substring(0, 22);
        String content = "{\"nickname\":\"" + nickname + "\", \"password\":\"password123\", \"publicId\":\"" + publicId + "\"}";

        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname));
    }
}
