package saas.app.engine.controller;

import com.rabbitmq.client.Return;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import saas.app.core.domain.PriceHistory;
import saas.app.core.domain.Product;
import saas.app.core.domain.ProductLink;
import saas.app.core.repository.PriceHistoryRepository;
import saas.app.core.repository.ProductLinkRepository;
import saas.app.core.repository.ProductRepository;
import saas.app.core.util.UrlUtils;
import saas.app.engine.dto.CreateProductRequest;
import saas.app.engine.dto.PricePointDTO;
import saas.app.engine.dto.ProductComparisonDTO;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //para que el front end pueda consultar sin errores de CORS

public class ProductController {

    private final ProductRepository productRepository;
    private final ProductLinkRepository linkRepository;
    private final PriceHistoryRepository historyRepository;




    @GetMapping("/{id}/comparison") //Basicamente va a ser el endpoint mas importante, compara los precios de un producto específico
    public ResponseEntity <ProductComparisonDTO> getProductComparison(@PathVariable Long id){
        //buscamos el producto
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        //buscamos las ofertas (a partir de los links) y ordenamos según el precio
        List <ProductLink> sortedLinks = linkRepository.findByProductIdOrderByCurrentPriceAsc(id);

        //mapeamos el DTO
        List <ProductComparisonDTO.OfferDTO> offerDTOS = sortedLinks.stream()
                .map(link -> ProductComparisonDTO.OfferDTO
                .builder()
                .storeName(link.getStoreName())
                .price(link.getCurrentPrice())
                .installments(link.getMaxInstallments())
                .hasFreeShipping(link.getHasFreeShipping())
                        .imageUrl(link.getImageUrl())
                        .availableSizes(link.getAvailableSizes())
                        .url(link.getUrl())
                .lastChecked(link.getLastChecked())
                .build()).sorted(Comparator.comparing(ProductComparisonDTO.OfferDTO::getPrice, Comparator
                        .nullsLast(Comparator.naturalOrder()))).collect(Collectors.toList());



        ProductComparisonDTO response = ProductComparisonDTO.builder()
                .productName(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .offers(offerDTOS)
                .build();

        return ResponseEntity.ok(response);

    }

    @GetMapping("/links/{linkId}/trend")
    public ResponseEntity <List<PricePointDTO>> getPriceTrend(@PathVariable Long linkId){
        if (!linkRepository.existsById(linkId)){
            return ResponseEntity.notFound().build();
        }

        List <PriceHistory> history = historyRepository.findByProductLinkIdOrderByDetectedAtAsc(linkId);

        List <PricePointDTO> trend = history.stream().map(h -> PricePointDTO.builder()
                .price(h.getPrice()).date(h.getDetectedAt()).build()).collect(Collectors.toList());

        return ResponseEntity.ok(trend);
    }

    @GetMapping("/search")
    public ResponseEntity <List<Product>> searchProducts(@RequestParam String query){
        return ResponseEntity.ok(productRepository.findByNameContainingIgnoreCase(query));
        //mas tarde se podria implementar busqueda mediante nombre de prenda o marca
    }

    @PostMapping("/with-link")
    @Transactional
    public ResponseEntity <Product> createProductWithLink(@RequestBody CreateProductRequest request){

        Product product;

        if (request.getProductId() != null){
            product = productRepository.findById(request.getProductId())
                    .orElseThrow( () -> new RuntimeException("Producto no encontrado con ID: "+ request.getProductId()));
            log.info("Agregando nuevo link al producto existente: {}", product.getName());
        } else {
            product = new Product();
            product.setName(request.getName());
            product.setBrand(request.getBrand());
            product.setCategory(request.getCategory());
            product.setGender(request.getGender());
            product.setIsActive(true);
            product.setCreatedAt(LocalDateTime.now());

            product = productRepository.save(product);
            log.info("Creado nuevo producto maestro: {}", product.getName());
        }



        ProductLink link = new ProductLink();
        //con este metodo del Utils vamos a poder limpiar el link automaticamente
        link.setUrl(UrlUtils.cleanStoreUrl(request.getUrl()));
        link.setProduct(product);
        link.setStoreName(request.getStoreName());
        link.setPriceSelector(request.getPriceSelector());
        link.setInstallmentsSelector(request.getInstallmentsSelector());
        link.setFreeShippingThreshold(request.getFreeShippingThreshold());
        link.setLastChecked(LocalDateTime.now());

        linkRepository.save(link);

        return ResponseEntity.status(HttpStatus.CREATED).body(product);

    }

        @DeleteMapping("/link/{linkId}")
        public ResponseEntity <Void> deleteLink(@PathVariable Long linkId){

        linkRepository.deleteById(linkId);

        log.info("Link ID {} eliminado manualmente", linkId);

        return ResponseEntity.noContent().build();

        }




    }







