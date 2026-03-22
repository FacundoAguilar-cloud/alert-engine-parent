package saas.app.engine.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoriteRequest {
    @NotNull(message = "El productId es obligatorio")
    private Long productId;
}
