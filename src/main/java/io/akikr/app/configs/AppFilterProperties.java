package io.akikr.app.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;


@ConfigurationProperties(prefix = "app.filter")
public record AppFilterProperties(@DefaultValue("false") boolean loggingEnabled,
                                  @DefaultValue("false") boolean includeRequestHeaders,
                                  @DefaultValue("false") boolean includeRequestBody,
                                  @DefaultValue("false") boolean includeResponseHeaders,
                                  @DefaultValue("false") boolean includeResponseBody,
                                  @DefaultValue("1000") int maxBodyLength) {
}
