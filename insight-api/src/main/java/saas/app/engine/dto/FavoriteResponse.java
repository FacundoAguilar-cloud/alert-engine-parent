package saas.app.engine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String brand;
    private String imageUrl;
    private BigDecimal minPrice;
    private String bestStore;
    private LocalDateTime addedAt;
}
