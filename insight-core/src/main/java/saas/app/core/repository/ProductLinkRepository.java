package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.ProductLink;

@Repository
public interface ProductLinkRepository extends JpaRepository<ProductLink, Long> {
    ProductLink findById();
}
