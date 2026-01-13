package saas.app.engine.scraper.dto;

import lombok.Builder;
import lombok.Data;
import saas.app.core.dto.SizeStockDTO;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ExtractorResult {
 private BigDecimal price;
 private Integer installments;
 private Boolean isAvailable;
 private String imageUrl;
 private List <SizeStockDTO> sizes;
}
