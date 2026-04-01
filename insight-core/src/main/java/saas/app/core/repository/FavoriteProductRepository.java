package saas.app.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import saas.app.core.domain.FavoriteProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteProductRepository extends JpaRepository<FavoriteProduct, Long> {

    List<FavoriteProduct> findByUser_Auth0Subject(String auth0Subject);

    Optional<FavoriteProduct> findByUserAuth0SubjectAndProductId(String auth0Subject, Long productId);

    boolean existsByUserAuth0SubjectAndProductId(String auth0Subject, Long productId);

    @Transactional
    @Modifying
    void deleteByUserAuth0SubjectAndProductId(String auth0Subject, Long productId);

}
