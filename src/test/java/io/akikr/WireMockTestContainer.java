package io.akikr;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

public abstract class WireMockTestContainer {

    protected static void setUpWireMockServerWithHttps(boolean isHttpsEnabled) {
        var wireMockServer = new GenericContainer<>("wiremock/wiremock:3.13.2")
                .withEnv("WIRE_MOCK_PORT", "--https-port 8443 --verbose")
                .withExposedPorts(8081, 8443)
                .waitingFor(new WaitAllStrategy());
        wireMockServer.start();

        if (wireMockServer.isRunning()) {
            String wireMockerServerBaseUrl = (isHttpsEnabled)
                    ? "https://" + wireMockServer.getHost() + ":" + wireMockServer.getMappedPort(8443)
                    : "http://" + wireMockServer.getHost() + ":" + wireMockServer.getMappedPort(8081);

            System.setProperty("app.external-service.client-base-url", wireMockerServerBaseUrl);
        }
    }
}
