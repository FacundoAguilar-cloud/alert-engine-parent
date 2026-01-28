package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.ProductLink;

import java.util.List;

@Repository
public interface ProductLinkRepository extends JpaRepository<ProductLink, Long> {


    List <ProductLink> findByProductIdOrderByCurrentPriceAsc(Long id);


}
