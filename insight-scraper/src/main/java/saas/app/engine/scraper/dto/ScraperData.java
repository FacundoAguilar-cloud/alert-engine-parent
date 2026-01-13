package saas.app.engine.scraper.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ScraperData {
    private BigDecimal price;
    private Integer installments;
    private Boolean hasFreeShipping;
    private Boolean isAvailable;
    private String imageUrl;
    //private BigDecimal  freeShippingThreshold;

}
