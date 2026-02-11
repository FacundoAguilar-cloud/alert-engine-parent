package saas.app.engine.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductCatalogDTO {
    private Long id;
    private String name;
    private String brand;
    private String category;
    private BigDecimal minPrice; //precio mas bajo del producto en cuestión
    private String bestOferrStore; //cual es la tienda que tiene ese precio más bajo
    private String imageUrl;
    private Integer totalOffers; //cuantas tiendas venden ese producto

}
