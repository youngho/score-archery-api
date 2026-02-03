package to.yho.score;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
class UserNicknameIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void changeNicknameRejectsDuplicate() throws Exception {
        String nickname1 = "dup_user_" + System.currentTimeMillis();
        String nickname2 = "dup_user2_" + System.currentTimeMillis();
        registerUser(nickname1);
        String publicId2 = registerUser(nickname2);

        mockMvc.perform(patch("/api/users/{publicId}/nickname", publicId2)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nickname", nickname1))))
                .andExpect(status().isConflict());
    }

    @Test
    void changeNicknameRejectsProfanity() throws Exception {
        String nickname = "clean_user_" + System.currentTimeMillis();
        String publicId = registerUser(nickname);

        mockMvc.perform(patch("/api/users/{publicId}/nickname", publicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nickname", "씨발러"))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeNicknameRejectsBlank() throws Exception {
        String nickname = "blank_user_" + System.currentTimeMillis();
        String publicId = registerUser(nickname);

        mockMvc.perform(patch("/api/users/{publicId}/nickname", publicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nickname", "   "))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void changeNicknameAllowsSameValue() throws Exception {
        String nickname = "same_user_" + System.currentTimeMillis();
        String publicId = registerUser(nickname);

        mockMvc.perform(patch("/api/users/{publicId}/nickname", publicId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nickname", nickname))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(nickname));
    }

    private String registerUser(String nickname) throws Exception {
        String content = objectMapper.writeValueAsString(Map.of(
                "nickname", nickname,
                "password", "password123"
        ));

        MvcResult result = mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        return jsonNode.get("publicId").asText();
    }
}
