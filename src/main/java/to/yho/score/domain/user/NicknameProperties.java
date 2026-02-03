package to.yho.score.domain.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "nickname")
public class NicknameProperties {

    private List<String> blockedWords = new ArrayList<>();
}
