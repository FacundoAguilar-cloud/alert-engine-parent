package saas.app.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductUpdateEvent implements Serializable { //por un tema de seguridad usamos este DTO y no la entidad, ademas de temas de peso y acoplamiento
    private Long productId;
    private Long linkId;
    private String storeName;

    private BigDecimal currentPrice;
    private Integer installments;
    private Boolean hasFreeShipping;

    private LocalDateTime timestamp;

    private Boolean isAvailable;
}
