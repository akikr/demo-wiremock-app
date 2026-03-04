package io.akikr;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.WaitAllStrategy;

public abstract class WireMockTestContainer {

    protected static void setUpWireMockServer() {
        var wireMockServer = new GenericContainer<>("wiremock/wiremock:2.32.0")
                .withExposedPorts(8080)
                .waitingFor(new WaitAllStrategy());
        wireMockServer.start();

        if (wireMockServer.isRunning()) {
            System.setProperty(
                    "app.external-service.client-base-url",
                    "http://" + wireMockServer.getHost() + ":" + wireMockServer.getMappedPort(8080));
        }
    }
}
