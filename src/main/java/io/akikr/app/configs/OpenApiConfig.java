package io.akikr.app.configs;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import java.util.Optional;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/// ### Configuration for OpenAPI documentation.
///
/// This configuration is marked as @Lazy to ensure that these beans are not
/// instantiated until they are first requested.
///
/// - Note: While springdoc-openapi is
/// already lazy by default (deferring heavy processing until the first API docs request),
/// this makes the lazy behavior of the configuration beans themselves explicit.

@Lazy
@Profile({"!prod"})
@Configuration(proxyBeanMethods = false)
public class OpenApiConfig {

    private final Environment env;
    private final Optional<BuildProperties> buildProperties;

    public OpenApiConfig(Environment env, ObjectProvider<BuildProperties> buildProps) {
        this.env = env;
        this.buildProperties = Optional.ofNullable(buildProps.getIfAvailable());
    }

    @Bean
    @Lazy
    public OpenAPI openApi() {
        // Prefer an explicit property that supports both with/without hyphen
        String version = Optional.ofNullable(env.getProperty("application-version"))
                .or(() -> Optional.ofNullable(env.getProperty("application.version")))
                // Then fall back to build-info (artifact version)
                .or(() -> buildProperties.map(BuildProperties::getVersion))
                // Lastly fall back to the JAR manifest implementation version or a default
                .or(() -> Optional.ofNullable(getClass().getPackage().getImplementationVersion()))
                .orElse("dev");

        return new OpenAPI()
                .info(new Info()
                        .title("Web Service API Documentation")
                        .version(version)
                        .description("API details of Web Service"));
    }

    @Bean
    @Lazy
    public OpenApiCustomizer dynamicServerCustomizer() {
        return openApi -> {
            // Active profiles
            String[] profiles = env.getActiveProfiles();
            String profileDesc = (profiles.length == 0) ? "default" : String.join(",", profiles);
            // Scheme (http/https)
            boolean ssl = env.getProperty("server.ssl.enabled", Boolean.class, false);
            String scheme = ssl ? "https" : "http";
            // Host: default to localhost, allow overriding (useful in containers/proxies)
            String host = env.getProperty("app.openapi.host", "localhost");
            // Port: prefer the actual runtime port if available
            String port = env.getProperty("server.port", "8080");
            // Context path if set (e.g., /bap)
            String contextPath = env.getProperty("server.servlet.context-path", "");
            String url = String.format("%s://%s:%s%s", scheme, host, port, contextPath);

            openApi.setServers(List.of(new Server().url(url).description(profileDesc)));
        };
    }
}
