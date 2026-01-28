package saas.app.engine.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    private Long productId;
    private String name;
    private String brand;
    private String category;
    private String gender;

    private String storeName;
    private String url;
    private String priceSelector;
    private String installmentsSelector;
    private BigDecimal freeShippingThreshold;
    private Boolean isActive;
}
