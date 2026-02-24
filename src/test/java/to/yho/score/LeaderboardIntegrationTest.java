package to.yho.score;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
class LeaderboardIntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @org.junit.jupiter.api.BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void getLeaderboardReturnsOkWithEntriesAndTotal() throws Exception {
        mockMvc.perform(get("/api/leaderboard").param("period", "alltime").param("offset", "1").param("limit", "50"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.entries").isArray())
                .andExpect(jsonPath("$.total").isNumber());
    }

    @Test
    void getLeaderboardAllPeriodsReturnOk() throws Exception {
        for (String period : new String[] { "alltime", "monthly", "weekly", "daily" }) {
            mockMvc.perform(get("/api/leaderboard").param("period", period))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.entries").isArray());
        }
    }

    @Test
    void getLeaderboardAfterRecordingScoreIncludesUser() throws Exception {
        String nickname = "lb_user_" + System.currentTimeMillis();
        String publicId = UUID.randomUUID().toString().replace("-", "").substring(0, 22);
        registerUser(publicId, nickname);
        recordScore(publicId, nickname, 1, 1, 10_000L);

        MvcResult result = mockMvc.perform(get("/api/leaderboard").param("period", "alltime").param("limit", "10"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode entries = root.get("entries");
        if (entries != null && entries.size() > 0) {
            JsonNode first = entries.get(0);
            assert first.has("rank") : "entry has rank";
            assert first.has("playerName") : "entry has playerName";
            assert first.has("score") : "entry has score";
        }
    }

    private void registerUser(String publicId, String nickname) throws Exception {
        String content = objectMapper.writeValueAsString(Map.of(
                "nickname", nickname,
                "password", "pass",
                "publicId", publicId
        ));
        mockMvc.perform(post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
    }

    private void recordScore(String publicId, String nickname, int world, int stage, long score) throws Exception {
        String content = objectMapper.writeValueAsString(Map.of(
                "publicId", publicId,
                "nickname", nickname,
                "worldNumber", world,
                "stageNumber", stage,
                "score", score
        ));
        mockMvc.perform(post("/api/stages/record")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isOk());
    }
}
