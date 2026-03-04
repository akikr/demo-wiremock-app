package io.akikr;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.SpringApplication;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

/// Mock server simulator for external service testing.
///
/// This class sets up and configures a WireMock server running in a Docker container to simulate
/// the behavior of an external service. It is intended for testing purposes only and allows developers
/// to test the application without making actual calls to external APIs.
///
/// ---
///
/// **How it works:**
///
/// - Starts a WireMock Docker container (version 3.13.2) with configurable HTTP/HTTPS ports
/// - Maps WireMock configuration files from classpath resources (`src/test/resources/wiremock`)
/// - Configures the application with the mock server base URL via system properties
/// - Sets the Spring profile to "test" for test-specific configurations
/// - Launches the Spring Boot application with the mock server active
///
///
/// **WireMock Configuration:**
///
/// WireMock mappings are defined in JSON files located at:
///
/// - `src/test/resources/wiremock/mappings/` - Contains API endpoint mappings
/// - `src/test/resources/wiremock/__files/` - Contains response body templates
///
/// ---
///
/// **Usage Example:**
/// ```text
/// Run with HTTP (default) runAppWithMockServerWithHttps(false);
/// ```
/// OR
/// ```text
/// with HTTPS runAppWithMockServerWithHttps(true);
/// ```
///
/// **System Properties Set:**
///
/// - `spring.profiles.active` - Set to "test" profile
/// - `app.external-service.client-base-url` - Base URL of the mock server with `/api` suffix
/// as define in WireMock resource mapping in the JSON files: `src/test/resources/wiremock/mappings`
///
/// ---
///
/// **Testing Only:**
///
/// This class should only be used in development and testing environments. Do not use this in production.
///
/// @see <a href="https://wiremock.org/">WireMock Documentation</a>
/// @see <a href="https://www.testcontainers.org/">Testcontainers Documentation</a>
public class WireMockAppSever {

    /// Entry point for running the application with a mocked external service server.
    ///
    /// Starts the WireMock container with HTTP support (no HTTPS) and launches the Spring Boot application.
    /// This is the main method to run for basic testing scenarios.
    public static void main() throws Exception {
        runAppWithMockServerWithHttps(false);
    }

    /// Runs the Spring Boot application with a configured WireMock mock server.
    ///
    /// This method:
    ///
    ///- Sets up the WireMock Docker container with optional HTTPS support
    ///- Configures the mock server base URL as a system property for the application
    ///- Activates the "test" Spring profile
    ///- Starts the Spring Boot application with the mock server active
    ///
    ///
    /// The external service base URL is automatically configured via the
    ///
    ///`app.external-service.client-base-url` system property,
    ///
    /// allowing the application to use the mock server instead of real external APIs.
    ///
    /// ---
    ///
    /// @param isHttpsEnabled `true` to enable HTTPS on port 8443, `false` for HTTP on port 8080
    /// @throws Exception if the WireMock server fails to start
    ///
    /// @see #setUpWireMockServerWithHttps(boolean)
    private static void runAppWithMockServerWithHttps(boolean isHttpsEnabled) throws Exception {
        var wireMockServerBaseUrl = setUpWireMockServerWithHttps(isHttpsEnabled);
        assertThat(wireMockServerBaseUrl).isNotEmpty();

        // Setup external-service.client-base-url as:
        // wireMockServerBaseUrl + WireMock resource mapping in the JSON files: [src/test/resources/wiremock/mappings]
        System.setProperty("app.external-service.client-base-url", wireMockServerBaseUrl + "/api");
        // Set spring profile to [test]
        System.setProperty("spring.profiles.active", "test");

        // Run the app
        SpringApplication.run(DemoWebApp.class);
    }

    /// Sets up and starts a WireMock Docker container with optional HTTPS support.
    ///
    /// This method:
    ///- Creates a GenericContainer for WireMock version 3.13.2
    ///- Configures HTTP (port 8080) and HTTPS (port 8443) ports based on the parameter
    ///- Maps WireMock configuration from classpath resources (`src/test/resources/wiremock`)
    ///- Starts the container and waits for it to be ready
    ///
    ///
    /// **Container Configuration:**
    ///- Image: `wiremock/wiremock:3.13.2`
    ///- Exposed Ports: `8080 (HTTP)` and `8443 (HTTPS)`
    ///- Volume Mapping: `wiremock/` → `/home/wiremock` (READ_ONLY)
    ///- Verbose Logging: Enabled for debugging
    ///
    /// ---
    ///
    /// @param isHttpsEnabled `true` to return HTTPS URL (port 8443),
    ///                       `false` to return HTTP URL (port 8080)
    ///
    /// @return the base URL of the mock server in format `http(s)://host:port`, or `null` if the server fails to
    ///
    /// @throws Exception if the container fails to start
    private static String setUpWireMockServerWithHttps(boolean isHttpsEnabled) throws Exception {
        var wireMockServer = new GenericContainer<>("wiremock/wiremock:3.13.2")
                .withEnv("WIREMOCK_OPTIONS", "--https-port 8443 --verbose")
                .withExposedPorts(8080, 8443)
                .withClasspathResourceMapping("wiremock", "/home/wiremock", BindMode.READ_ONLY)
                .waitingFor(new WaitAllStrategy());
        wireMockServer.start();

        if (wireMockServer.isRunning()) {
            return (isHttpsEnabled)
                    ? "https://" + wireMockServer.getHost() + ":" + wireMockServer.getMappedPort(8443)
                    : "http://" + wireMockServer.getHost() + ":" + wireMockServer.getMappedPort(8080);
        }
        return null;
    }
}
