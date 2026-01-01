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
public class PriceUpdateEvent implements Serializable { //por un tema de seguridad usamos este DTO y no la entidad, ademas de temas de peso y acoplamiento
    private Long productId;
    private String productName;
    private String storeName;
    private String url;
    private BigDecimal currentPrice;
    private String currency;
    private String installmentsInfo;
    private Boolean freeShipping;

    private LocalDateTime timestamp;
}
