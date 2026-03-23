package saas.app.engine.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import saas.app.core.domain.FavoriteProduct;
import saas.app.core.repository.FavoriteProductRepository;
import saas.app.core.repository.ProductRepository;
import saas.app.core.repository.UserRepository;
import saas.app.engine.dto.FavoriteResponse;
import saas.app.engine.service.SecurityService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final FavoriteProductRepository favoriteProductRepository;
    private final SecurityService securityService;


    @GetMapping
    public ResponseEntity <List<FavoriteResponse>> getMyFavorites(){
        String auth0Subject = securityService.getCurrentUserAuth0Subject();
        ensureUserExists(auth0Subject);

        List<FavoriteProduct> favorites = favoriteProductRepository.findByUserAuth0Subject(auth0Subject);

    }
}
