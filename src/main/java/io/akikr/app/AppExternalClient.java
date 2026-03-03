package io.akikr.app;

import io.akikr.app.models.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AppExternalClient {

    private static final Logger log = LoggerFactory.getLogger(AppExternalClient.class);
    private final RestClient restClient;

    public AppExternalClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public String getJsonData(String path) {
        log.info("Invoked AppService#getJsonData method for path:[{}]", path);
        var responseEntity = restClient.get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Successfully returned response for path:[{}]", path);
            return responseEntity.getBody();
        }
        log.error("Failed to retrieve response for path:[{}]", path);
        return null;
    }

    public String postJsonData(JsonData data) {
        log.info("Invoked AppService#postJsonData method for data:[{}]", data);
        ResponseEntity<String> responseEntity = restClient.post()
                .uri("/anything")
                .contentType(MediaType.APPLICATION_JSON)
                .body(data)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toEntity(String.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            log.info("Successfully returned response for post JSON data");
            return responseEntity.getBody();
        }
        log.error("Failed to retrieve response post JSON data");
        return null;
    }
}
