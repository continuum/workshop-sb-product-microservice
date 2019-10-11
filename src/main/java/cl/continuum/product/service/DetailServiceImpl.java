package cl.continuum.product.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile("prod")
public class DetailServiceImpl implements DetailService {

    private static final String SERVICE_HOST = "http://localhost:8082";

    @Bean
    private RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public Map<String, Object> get(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        ResponseEntity<Map> resp = restTemplate().getForEntity(String.format("%s/api/v1/rating", SERVICE_HOST), Map.class, params);
        if (resp.getStatusCode().value() == 200) {
            return resp.getBody();
        } else {
            return null;
        }
    }
}
