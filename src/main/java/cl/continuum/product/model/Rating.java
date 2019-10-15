package cl.continuum.product.model;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Rating {
    private Long id;
    private String productName;
    private Integer rating;
    private Integer commentsCount;
    private Long price;
    private String ecommerce;
}
