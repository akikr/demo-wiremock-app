package io.akikr.app;

import io.akikr.app.models.JsonData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AppService {

    private static final Logger log = LoggerFactory.getLogger(AppService.class);
    private final AppExternalClient appExternalClient;;

    public AppService(AppExternalClient appExternalClient) {
        this.appExternalClient = appExternalClient;
    }

    public ResponseEntity<String> getJsonData() {
        log.info("Invoked AppService#getJsonData method");
         String data = appExternalClient.getJsonData("/json");
         if (data == null || data.isEmpty()) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND)
                     .body(Map.of("msg", "JSON data not found").toString());
         }
        return ResponseEntity.ok()
                .body(data);
    }

    public ResponseEntity<String> postJsonData(JsonData data) {
        log.info("Invoked AppService#postJsonData method");
        if (data == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("msg", "Invalid JSON data").toString());
        }
        String response = appExternalClient.postJsonData(data);
        if (response == null || response.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("msg", "JSON data not found").toString());
        }
        return ResponseEntity.ok()
                .body(response);
    }
}
