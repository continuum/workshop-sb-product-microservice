package cl.continuum.product.resource;

import cl.continuum.product.model.Product;
import cl.continuum.product.service.DetailService;
import cl.continuum.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/products", produces = "application/json")
public class ProductResource {

    @Autowired
    private ProductService productService;

    @Autowired
    private DetailService detailService;

    @GetMapping(path = "")
    ResponseEntity<?> getProducts(@RequestParam(required = false, name = "name") String name) {
        List<Product> products = productService.list(name);
        if (products == null || products.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(products);
    }

    @PostMapping(path = "", consumes = "application/json")
    ResponseEntity<?> createProduct(@Valid @RequestBody(required = true) Product product) {
        product = productService.add(product);
        return ResponseEntity.status(201).body(product);
    }

    @GetMapping(path = "/{id}")
    ResponseEntity<?> getProduct(@PathVariable(required = false, name = "id") Long id) {
        Product product = productService.get(id);
        if (product == null) {
            return ResponseEntity.status(404).build();
        }
        List detail = detailService.get(product.getName());
        product.setDetail(detail);
        return ResponseEntity.ok(product);
    }
}
