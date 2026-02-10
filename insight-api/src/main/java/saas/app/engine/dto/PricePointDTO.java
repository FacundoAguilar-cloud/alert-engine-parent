package saas.app.engine.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class PricePointDTO {
    private BigDecimal price;
    private LocalDateTime date;


}
