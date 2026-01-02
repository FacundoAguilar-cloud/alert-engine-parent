package saas.app.engine.scraper.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class ScraperData {
    private BigDecimal price;
    private Integer installments;
    private Boolean offersFreeShipping;
    private Double  freeShippingAmount;
    private String stockStatus;

}
