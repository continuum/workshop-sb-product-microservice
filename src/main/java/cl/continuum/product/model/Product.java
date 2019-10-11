package cl.continuum.product.model;

import lombok.*;

import javax.validation.constraints.*;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Product {
    private Long id;

    @NotEmpty(message = "Please provide a name")
    @Size(min=3, max=200)
    private String name;

    @NotNull(message = "Please provide a price")
    @Min(1)
    @Max(1000000)
    private Long price;

    private Map<String, Object> detail;
}
