package saas.app.engine.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Data
public class CreateProductRequest {
    private Long productId;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;

    @NotBlank(message = "La marca es obligatoria")
    private String brand;

    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    private String gender;

    @NotBlank(message = "El nombre de la tienda es obligatorio")
    private String storeName;

    @NotBlank(message = "La URL de la tienda es obligatoria")
    @URL(message = "Debe ser una URL válida")
    private String url;

    private String priceSelector;

    private String installmentsSelector;

    @PositiveOrZero(message = "El umbral de envío gratis no puede ser negativo")
    private BigDecimal freeShippingThreshold;

    private Boolean isActive;
}
