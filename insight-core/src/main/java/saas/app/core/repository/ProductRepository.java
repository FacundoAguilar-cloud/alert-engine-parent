package saas.app.core.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.Product;


import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List <Product>  findByNameContainingIgnoreCase(String query);
    
    //vamos a establecer consula flexible (nombre, marca o categoria segun se proporcione)

    @Query("SELECT p FROM Product p WHERE " +
            "(:q IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :q, '%'))) AND " +
            "(:brand IS NULL OR LOWER(p.brand) = LOWER(:brand)) AND " +
            "(:cat IS NULL OR LOWER(p.category) = LOWER(:cat))")
    Page<Product> findWithFilters(
            @Param("q") String query,
            @Param("brand") String brand,
            @Param("cat") String category,
            Pageable pageable);
}
