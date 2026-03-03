package io.akikr.app.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.external-service")
public record AppClientProperties(
        @DefaultValue("http://localhost:8081/api") String clientBaseUrl,
        @DefaultValue("2000") int clientConnectTimeout,
        @DefaultValue("2000") int clientReadTimeout,
        @DefaultValue("false") boolean clientLoggingEnabled,
        @DefaultValue("false") boolean clientIncludeRequestHeaders,
        @DefaultValue("false") boolean clientIncludeRequestBody,
        @DefaultValue("false") boolean clientIncludeResponseHeaders,
        @DefaultValue("false") boolean clientIncludeResponseBody,
        @DefaultValue("1000") int clientMaxBodyLength
) {
}
