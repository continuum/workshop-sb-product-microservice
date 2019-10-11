package cl.continuum.service;

import cl.continuum.product.model.Product;

import java.util.List;

public interface ProductService {
    Product add(Product product);
    List<Product> list(String name);
    Product get(Long id);
}
