package io.akikr.app.configs;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static java.util.Objects.nonNull;

@Component
public class AppClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AppClientLoggingInterceptor.class);

    private final AppClientProperties appClientProperties;

    public AppClientLoggingInterceptor(AppClientProperties appClientProperties) {
        this.appClientProperties = appClientProperties;
    }

    /// Intercepts outgoing REST requests to log request and response details
    ///
    /// @param request   the HTTP request to execute
    /// @param body      the body of the request
    /// @param execution the request execution
    ///
    /// @return the HTTP response after execution
    /// @throws IOException if an I/O error occurs
    ///
    /// @apiNote This method uses static logging methods and [BufferingClientHttpResponseWrapper]
    /// to return wrapped response to allow reading the body again
    ///
    @NonNull
    @Override
    public ClientHttpResponse intercept(@NonNull HttpRequest request, byte [] body, @NonNull ClientHttpRequestExecution execution) throws IOException {
        if (appClientProperties.clientLoggingEnabled()) logRequest(request, body);
        var response = execution.execute(request, body);
        return logResponse(response);
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.debug("Client REQUEST: {} {}", request.getMethod(), request.getURI());

        var headers = "[Headers not logged]";
        if (appClientProperties.clientIncludeRequestHeaders()) {
            headers = request.getHeaders().toString();
        }
        log.debug("Client REQUEST Headers: {}", headers);

        var requestBody = "[Not Logged]";
        if (appClientProperties.clientIncludeRequestBody() && nonNull(body)) {
            requestBody = new String(body, StandardCharsets.UTF_8);
            if (requestBody.length() > appClientProperties.clientMaxBodyLength()) {
                requestBody = requestBody.substring(0, appClientProperties.clientMaxBodyLength()) + "...[truncated]";
            }
        }
        log.debug("Client REQUEST Body: {}", (requestBody.isEmpty()) ? "[No-Body]" : requestBody);
    }

    private ClientHttpResponse logResponse(ClientHttpResponse response) throws IOException {
        log.debug("Client RESPONSE Status: {}", response.getStatusCode());

        byte[] responseBody = response.getBody().readAllBytes();
        if (appClientProperties.clientLoggingEnabled()) {
            var headers = "[Headers not logged]";
            if (appClientProperties.clientIncludeResponseHeaders()) {
                headers = response.getHeaders().toString();
            }
            log.debug("Client RESPONSE Headers: {}", headers);

            var responseStr = "[Not Logged]";
            if (appClientProperties.clientIncludeResponseBody()) {
                responseStr =  new String(responseBody, StandardCharsets.UTF_8);
                if (responseStr.length() > appClientProperties.clientMaxBodyLength()) {
                    responseStr = responseStr.substring(0, appClientProperties.clientMaxBodyLength()) + "...[truncated]";
                }
            }
            log.debug("Client RESPONSE Body: {}", (responseStr.isEmpty()) ? "[No-Body]" : responseStr);
        }
        // Return wrapped response to allow reading the body again
        return new BufferingClientHttpResponseWrapper(response, responseBody);
    }

    static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {

        private final ClientHttpResponse response;
        private final byte[] body;

        BufferingClientHttpResponseWrapper(ClientHttpResponse response, byte[] body) {
            this.response = response;
            this.body = body;
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }

        @Override
        public InputStream getBody() throws IOException {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }
    }
}
