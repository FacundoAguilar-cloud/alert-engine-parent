package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.Product;

import java.util.List;

@Repository
public interface MonitoredSiteRepository extends JpaRepository <Product, Long> {
    List <Product> findByActiveTrue();

    List<Product> id(Long id);
}
