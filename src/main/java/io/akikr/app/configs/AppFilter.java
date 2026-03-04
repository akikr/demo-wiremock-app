package io.akikr.app.configs;

import static io.akikr.app.configs.AppFilterLogger.logRequest;
import static io.akikr.app.configs.AppFilterLogger.logResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.jspecify.annotations.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

public class AppFilter extends OncePerRequestFilter {

    private final AppFilterProperties appFilterProperties;

    public AppFilter(AppFilterProperties appFilterProperties) {
        this.appFilterProperties = appFilterProperties;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = requestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = responseWrapper(response);
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
        } finally {
            if (appFilterProperties.loggingEnabled()) logRequest(requestWrapper, appFilterProperties);
            if (appFilterProperties.loggingEnabled()) logResponse(responseWrapper, appFilterProperties);

            responseWrapper.copyBodyToResponse();
        }
    }

    private ContentCachingRequestWrapper requestWrapper(HttpServletRequest httpServletRequest) {
        if (httpServletRequest instanceof ContentCachingRequestWrapper requestWrapper) {
            return requestWrapper;
        }
        return new ContentCachingRequestWrapper(httpServletRequest);
    }

    private ContentCachingResponseWrapper responseWrapper(HttpServletResponse httpServletResponse) {
        if (httpServletResponse instanceof ContentCachingResponseWrapper responseWrapper) {
            return responseWrapper;
        }
        return new ContentCachingResponseWrapper(httpServletResponse);
    }
}
