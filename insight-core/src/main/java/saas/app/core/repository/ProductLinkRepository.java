package saas.app.core.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.ProductLink;

import java.util.List;

@Repository
public interface ProductLinkRepository extends JpaRepository<ProductLink, Long> {


    List <ProductLink> findByProductIdOrderByCurrentPriceAsc(Long id);


    @Query("SELECT l FROM ProductLink l " +
            "WHERE l.currentPrice IS NOT NULL " +
            "AND l.previousPrice IS NOT NULL " +
            "AND l.currentPrice < l.previousPrice " +
            "ORDER BY l.currentPrice ASC")
    List<ProductLink> findTopDeals(Pageable pageable);


}
