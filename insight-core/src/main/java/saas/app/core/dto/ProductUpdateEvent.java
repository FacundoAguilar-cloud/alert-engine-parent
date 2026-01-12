package saas.app.core.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateEvent implements Serializable { //por un tema de seguridad usamos este DTO y no la entidad, ademas de temas de peso y acoplamiento
    private Long productId;
    private Long linkId;
    private String storeName;
    private String imageUrl;

    private BigDecimal currentPrice;
    private Integer installments;
    private Boolean hasFreeShipping;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lastChecked;

    private Boolean isAvailable;
}
