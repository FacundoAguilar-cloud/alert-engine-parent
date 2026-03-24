package saas.app.engine.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saas.app.core.domain.FavoriteProduct;
import saas.app.core.domain.User;
import saas.app.core.exception.EntityNotFoundException;
import saas.app.core.repository.FavoriteProductRepository;
import saas.app.core.repository.ProductRepository;
import saas.app.core.repository.UserRepository;
import saas.app.engine.dto.FavoriteRequest;
import saas.app.engine.dto.FavoriteResponse;
import saas.app.engine.service.SecurityService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

        List <FavoriteResponse> response = favorites.stream().map(this::mapToResponse).collect(Collectors.toList());

        return ResponseEntity.ok(response);

    }

    @PostMapping
    public ResponseEntity <FavoriteResponse> addFavorite(@Valid @RequestBody FavoriteRequest request){
        String auth0Subject = securityService.getCurrentUserAuth0Subject();
        User user = ensureUserExists(auth0Subject);

        if(favoriteProductRepository.existsByUserAuth0SubjectAndProductId(auth0Subject, request.getProductId())){
            throw new IllegalArgumentException("Este producto ya esta en favoritos");
        }

        var product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        FavoriteProduct favorite = FavoriteProduct.builder()
                .user(user)
                .product(product)
                .build();

        FavoriteProduct saved = favoriteProductRepository.save(favorite);

        log.info("Producto {} agregado a favoritos para usuario {}", request.getProductId(), auth0Subject);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(saved));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity <Void> removeFavorite(@PathVariable Long productId){
     String auth0Subject = securityService.getCurrentUserAuth0Subject();

     if (!favoriteProductRepository.existsByUserAuth0SubjectAndProductId(auth0Subject, productId)){
         throw new EntityNotFoundException("Favorito no encontrado");
     }

     favoriteProductRepository.deleteByUserAuth0SubjectAndProductId(auth0Subject, productId);
     log.info("Producto {} eliminado de favoritos para usuario {}", productId, auth0Subject);

     return ResponseEntity.noContent().build();
    }

    private User ensureUserExists(String auth0Subject) {
        return userRepository.findByAuth0Subject(auth0Subject).orElseGet(() -> {
         User newUser = User.builder()
            .auth0Subject(auth0Subject)
            .email(securityService.getCurrentUserEmail())
            .name(securityService.getCurrentUserName())
            .build();

            log.info("Nuevo usuario creado automaticamente: {}", auth0Subject);

                return userRepository.save(newUser);

        });
    }

    private FavoriteResponse mapToResponse(FavoriteProduct favorite){
        var product = favorite.getProduct();

        var bestLink = product.getLinks().stream().
        filter(l -> l.getCurrentPrice() != null)
                .min(Comparator.comparing(l -> l.getCurrentPrice()))
                .orElse(null);

        return FavoriteResponse.builder()
                .id(favorite.getId())
                .productId(product.getId())
                .productName(product.getName())
                .brand(product.getBrand())
                .imageUrl(bestLink != null ? bestLink.getImageUrl() : null)
                .minPrice(bestLink != null ? bestLink.getCurrentPrice() : null)
                .bestStore(bestLink != null ? bestLink.getStoreName() : null)
                .addedAt(favorite.getAddedAt())
                .build();
    }



}
