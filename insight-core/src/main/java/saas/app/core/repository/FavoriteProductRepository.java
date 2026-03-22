package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import saas.app.core.domain.FavoriteProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {

    List<FavoriteProduct> findByUserAuth0Subject(String auth0Subject);

    Optional<FavoriteProduct> findUserByAuth0SubjectAndProductId(String auth0Subject, Long productId);

    boolean existsByUserAuth0SubjectAndProductId(String auth0Subject, Long productId);

    void deleteByUserAuth0SubjectAndProductId(String auth0Subject, Long productId);

}
