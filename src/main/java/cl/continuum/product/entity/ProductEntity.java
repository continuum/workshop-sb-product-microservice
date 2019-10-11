package cl.continuum.product.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Entity
public class ProductEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Long price;
    private LocalDateTime created;
    private LocalDateTime updated;
}
