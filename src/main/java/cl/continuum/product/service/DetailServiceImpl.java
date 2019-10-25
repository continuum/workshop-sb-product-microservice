package cl.continuum.product.service;

import cl.continuum.product.model.Rating;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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
    public List get(String name) {
        try {
            String url = String.format("%s/api/v1/rating", SERVICE_HOST);
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url).queryParam("name", name);
            //ResponseEntity<List> resp = restTemplate().getForEntity(builder.toUriString(), List.class);

            ResponseEntity<List<Rating>> resp =  restTemplate().exchange(
                    String.format("%s?name=%s",url,name),
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Rating>>(){});
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
