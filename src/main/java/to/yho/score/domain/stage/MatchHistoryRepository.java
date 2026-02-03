package to.yho.score.domain.stage;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchHistoryRepository extends JpaRepository<MatchHistory, Long> {

    boolean existsByPublicId(String publicId);
}
