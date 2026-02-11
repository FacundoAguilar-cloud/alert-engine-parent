package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.PriceHistory;
import saas.app.core.domain.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface PriceHistoryRepository extends JpaRepository <PriceHistory, Long> {

    List<PriceHistory> findByProductLinkIdOrderByDetectedAtAsc(Long linkId);

    Optional<PriceHistory> findFirstByProductLinkIdOrderByDetectedAtDesc(Long linkId);
}
