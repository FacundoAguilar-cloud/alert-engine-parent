package saas.app.core.domain;

import jakarta.persistence.*;

@Entity
public class ProductLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;

    private String url;

    private String priceSelector;

    private String  installmnetsSelector; //va a buscar lo de las cuotas basicamente

    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
}
