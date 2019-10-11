package cl.continuum.product.service;

import cl.continuum.product.model.Product;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("ProductServiceMock")
public class ProductServiceMock implements ProductService {

    private static Map<Long, Product> productMap = new HashMap<>();

    static {
        Product p1 = new Product(1L, "Nintendo Switch", 250000L, null);
        Product p2 = new Product(2L, "Nintendo Switch Lite", 200000L, null);
        Product p3 = new Product(3L, "Xbox One X", 300000L, null);
        productMap.put(p1.getId(), p1);
        productMap.put(p2.getId(), p2);
        productMap.put(p3.getId(), p3);
    }

    @Override
    public Product add(Product product) {
        product.setId(productMap.values().size()+1L);
        productMap.put(product.getId(), product);
        return product;
    }

    @Override
    public List<Product> list(String name) {
        if (name != null && !name.isEmpty()) {
            String namelw = name.toLowerCase();
            return productMap.values()
                    .stream()
                    .filter((p) -> p.getName().toLowerCase().contains(namelw))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>(productMap.values());
        }
    }

    @Override
    public Product get(Long id) {
        Product p = productMap.get(id);
        if (p == null) {
            return null;
        }
        return new Product(p.getId(), p.getName(), p.getPrice(), null);
    }
}
