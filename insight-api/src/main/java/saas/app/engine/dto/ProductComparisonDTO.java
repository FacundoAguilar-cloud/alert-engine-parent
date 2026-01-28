package saas.app.engine.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ProductComparisonDTO {
    private String productName;
    private String brand;
    private String category;
    private List <OfferDTO> offers;


    @Data
    @Builder
    public static class OfferDTO{
    private String storeName;
    private BigDecimal price;
    private Integer installments;
    private Boolean hasFreeShipping;
    private String imageUrl;
    private String availableSizes;
    private String url;
    private LocalDateTime lastChecked;
    }
}
