package io.akikr.app;

import io.akikr.app.models.JsonData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api")
public class AppController implements AppOperations {

    private final AppService appService;

    public AppController(AppService appService) {
        this.appService = appService;
    }

    @Override
    @GetMapping(path = "/data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getData(){
        return appService.getJsonData();
    }

    @Override
    @PostMapping(path = "/data", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postData(@RequestBody JsonData data){
        return appService.postJsonData(data);
    }
}
