package cl.continuum.product.repository;

import cl.continuum.product.entity.ProductEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductCrudRepository extends CrudRepository<ProductEntity, Long> {
    List<ProductEntity> findByName(String name);
}
