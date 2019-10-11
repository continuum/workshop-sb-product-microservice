package cl.continuum.product.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
        try {
            String url = String.format("%s/api/v1/rating", SERVICE_HOST);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("name", name);
            ResponseEntity<Map> resp = restTemplate().getForEntity(builder.toUriString(), Map.class);
            if (resp.getStatusCode().value() != 200) {
                throw new Exception("Gateway Error");
            }
            return resp.getBody();
        } catch(Exception ex) {
            System.err.println(ex.getMessage() + " : " + ex.getCause());
            return null;
        }
    }
}
