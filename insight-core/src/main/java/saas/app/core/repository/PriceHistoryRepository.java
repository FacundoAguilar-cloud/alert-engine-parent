package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.PriceHistory;
import saas.app.core.domain.Product;

@Repository
public interface PriceHistoryRepository extends JpaRepository <PriceHistory, Long> {
}
