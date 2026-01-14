package saas.app.engine.scraper.dto;

import lombok.Builder;
import lombok.Data;
import saas.app.core.dto.SizeStockDTO;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
public class ScraperData {
    private BigDecimal price;
    private Integer installments;
    private Boolean hasFreeShipping;
    private Boolean isAvailable;
    private String imageUrl;
    private List<SizeStockDTO> sizes;
    //private BigDecimal  freeShippingThreshold;

}
