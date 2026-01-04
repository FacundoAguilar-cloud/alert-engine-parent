package saas.app.core.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private String url;

    private String priceSelector;
    private String  installmnetsSelector; //va a buscar lo de las cuotas basicamente

    private Integer maxInstallments; //cantidad de cuotas maximas

    private BigDecimal currentPrice;

    private Boolean hasFreeShipping;

    private BigDecimal freeShippingThreshold;

    private LocalDateTime lastStockChecked;

    @ManyToOne
    @JoinColumn(name = "productId")
    @JsonIgnore //cambiar si es contraproducente
    private Product product;
}
