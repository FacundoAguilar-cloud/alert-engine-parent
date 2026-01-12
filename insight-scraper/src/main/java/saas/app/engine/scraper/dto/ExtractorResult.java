package saas.app.engine.scraper.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExtractorResult {
 private BigDecimal price;
 private Integer installments;
 private Boolean isAvailable;
 private String imageUrl;
}
