package saas.app.engine.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductDealDTO {

    private Long productId;

    private String productName;

    private String brand;

    private String storeName;

    private BigDecimal currentPrice;

    private BigDecimal oldPrice;

    private String imageUrl;

    public double getDiscountPercentage() {
        if (oldPrice == null || oldPrice.compareTo(BigDecimal.ZERO) <= 0){
            return 0.0;
        }
        return oldPrice.subtract(currentPrice)
                .divide(oldPrice, 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }
}
