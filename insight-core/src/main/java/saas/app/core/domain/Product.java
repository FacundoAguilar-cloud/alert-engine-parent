package saas.app.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String brand;

    private String category;

    private String gender;

    private String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List <ProductLink> links;

    //agregar luego un dato que contenga una imagen de referencia para el catalogo de cierto producto

}
