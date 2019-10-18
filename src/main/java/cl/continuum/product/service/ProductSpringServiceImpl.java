package cl.continuum.product.service;

import cl.continuum.product.entity.ProductEntity;
import cl.continuum.product.model.Product;
import cl.continuum.product.repository.ProductCrudRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Profile("prod")
public class ProductSpringServiceImpl implements ProductService {

    @Autowired
    private ProductCrudRepository productRepository;

    @Override
    public Product add(Product product) {
        ProductEntity productEntity = new ProductEntity();
        BeanUtils.copyProperties(product, productEntity);
        productEntity.setCreated(LocalDateTime.now());

        ProductEntity savedEntity = productRepository.save(productEntity);
        BeanUtils.copyProperties(savedEntity, product);

        return product;
    }

    @Override
    public List<Product> list(String name) {
        Iterable<ProductEntity> entities = null == name ? productRepository.findAll() : productRepository.findByNameLike("%" + name + "%");
        List<Product> products = new ArrayList<>();
        entities.forEach(entity -> {
            Product product = new Product();
            BeanUtils.copyProperties(entity, product);
            products.add(product);
        });

        return products;
    }

    @Override
    public Product get(Long id) {
        Product product = new Product();
        Optional<ProductEntity> productEntity = productRepository.findById(id);
        productEntity.ifPresent(entity -> {
            BeanUtils.copyProperties(entity, product);
        });

        if (null == product.getId()) return null;
        return product;
    }
}
