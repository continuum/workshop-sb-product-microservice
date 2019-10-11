package cl.continuum.product.resource;

import cl.continuum.product.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/products")
public class ProductResource {

    @GetMapping(path = "")
    ResponseEntity<?> getProducts() {

        System.out.println("get products");

        return null;
    }
}
