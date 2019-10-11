package cl.continuum.product.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Product {
    private Long id;
    private String name;
    private Long price;
}
