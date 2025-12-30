package saas.app.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteChangeEvent implements Serializable { //por un tema de seguridad usamos este DTO y no la entidad, ademas de temas de peso y acoplamiento
    private Long siteId;
    private String siteName;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;
}
