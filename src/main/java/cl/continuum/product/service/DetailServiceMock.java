package cl.continuum.product.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Profile("!prod")
public class DetailServiceMock implements DetailService {

    @Override
    public List get(String name) {
        Map<String, Object> detail = new HashMap<>();
        //{id: 100, rating: 4, comments_count: 100, price: 1200, ecommerce: “amazon”}
        detail.put("id", 1);
        detail.put("rating", 4);
        detail.put("comments_count", 100);
        detail.put("price", 100);
        detail.put("ecommerce", "Amazon");
        return new ArrayList<Object>(detail.values());
    }
}
