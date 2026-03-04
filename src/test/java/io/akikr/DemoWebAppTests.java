package io.akikr;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.properties"})
class DemoWebAppTests extends WireMockTestContainer {

    @Autowired
    private ApplicationContext applicationContext;

    @BeforeAll
    static void setup() {
        setUpWireMockServerWithHttps(false);
    }

    @Test
    @DisplayName("Test Spring Web Application Context Loads")
    void contextLoads() {
        // Verify Spring application context is not null
        assertThat(applicationContext).isNotNull();

        // Verify the main application class is loaded
        assertThat(applicationContext.getBean("demoWebApp")).isInstanceOf(DemoWebApp.class);

        // Verify the WireMock Server is set up and external-service url is configured
        String externalServiceUrl = System.getProperty("app.external-service.client-base-url");
        assertThat(externalServiceUrl).isNotNull();
        assertThat(externalServiceUrl).isNotBlank();
        System.out.println("External Service URL: " + externalServiceUrl);
    }
}
