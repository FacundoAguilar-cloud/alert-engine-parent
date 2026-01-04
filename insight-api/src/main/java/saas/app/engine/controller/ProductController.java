package saas.app.engine.controller;

import com.rabbitmq.client.Return;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import saas.app.core.domain.Product;
import saas.app.core.domain.ProductLink;
import saas.app.core.repository.ProductLinkRepository;
import saas.app.core.repository.ProductRepository;
import saas.app.engine.dto.CreateProductRequest;
import saas.app.engine.dto.ProductComparisonDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") //para que el front end pueda consultar sin errores de CORS

public class ProductController {

    private final ProductRepository productRepository;
    private final ProductLinkRepository linkRepository;



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
                .freeShipping(link.getHasFreeShipping())
                .url(link.getUrl())
                .lastUpdated(link.getLastStockChecked())
                .build()
                ).toList();

        ProductComparisonDTO response = ProductComparisonDTO.builder()
                .productName(product.getName())
                .brand(product.getBrand())
                .category(product.getCategory())
                .offers(offerDTOS)
                .build();

        return ResponseEntity.ok(response);

    }

    @GetMapping("/search")
    public ResponseEntity <List<Product>> searchProducts(@RequestParam String query){
        return ResponseEntity.ok(productRepository.findByNameContainingIgnoreCase(query));
        //mas tarde se podria implementar busqueda mediante nombre de prenda o marca
    }

    @PostMapping("/with-link")
    @Transactional
    public ResponseEntity <Product> createProductWithLink(@RequestBody CreateProductRequest request){
        Product product = new Product();
        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setCategory(request.getCategory());
        product.setGender(request.getGender());
        product.setIsActive(true);
        product.setCreatedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);

        ProductLink link = new ProductLink();
        link.setProduct(savedProduct);
        link.setStoreName(request.getStoreName());
        link.setUrl(request.getUrl());
        link.setPriceSelector(request.getPriceSelector());
        link.setInstallmnetsSelector(request.getInstallmentsSelector());
        link.setFreeShippingThreshold(request.getFreeShippingThreshold());
        link.setLastStockChecked(LocalDateTime.now());

        linkRepository.save(link);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);

    }



    }







