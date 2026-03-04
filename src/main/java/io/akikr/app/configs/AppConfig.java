package io.akikr.app.configs;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(value = {AppClientProperties.class, AppFilterProperties.class})
public class AppConfig {

    private final AppClientProperties appClientProperties;
    private final AppFilterProperties appFilterProperties;

    public AppConfig(AppClientProperties appClientProperties, AppFilterProperties appFilterProperties) {
        this.appClientProperties = appClientProperties;
        this.appFilterProperties = appFilterProperties;
    }

    @Bean
    public FilterRegistrationBean<AppFilter> appFilter() {
        FilterRegistrationBean<AppFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new AppFilter(appFilterProperties));
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public RestClient restClient(RestClient.Builder builder, AppClientLoggingInterceptor appClientLoggingInterceptor) {
        SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        simpleClientHttpRequestFactory.setConnectTimeout(appClientProperties.clientConnectTimeout());
        simpleClientHttpRequestFactory.setReadTimeout(appClientProperties.clientReadTimeout());

        return builder.requestFactory(simpleClientHttpRequestFactory)
                .baseUrl(appClientProperties.clientBaseUrl())
                .requestInterceptor(appClientLoggingInterceptor)
                .build();
    }
}
