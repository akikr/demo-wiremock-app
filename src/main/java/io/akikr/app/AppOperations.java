package io.akikr.app;

import io.akikr.app.models.JsonData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "AppController", description = "Endpoints for all Web application APIs")
public interface AppOperations {

    @Operation(summary = "To get some JSON data")
    @GetMapping(path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> getData();

    @Operation(summary = "To post some JSON data")
    @PostMapping(path = "/data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<String> postData(@RequestBody JsonData data);
}
