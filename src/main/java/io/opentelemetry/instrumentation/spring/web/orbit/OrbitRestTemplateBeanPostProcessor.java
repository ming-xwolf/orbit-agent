package io.opentelemetry.instrumentation.spring.web.orbit;

import io.opentelemetry.api.OpenTelemetry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;


import java.util.List;

public class OrbitRestTemplateBeanPostProcessor implements BeanPostProcessor {
    private final ObjectProvider<OpenTelemetry> openTelemetryProvider;

    OrbitRestTemplateBeanPostProcessor(ObjectProvider<OpenTelemetry> openTelemetryProvider) {
        this.openTelemetryProvider = openTelemetryProvider;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (!(bean instanceof RestTemplate)) {
            return bean;
        }

        RestTemplate restTemplate = (RestTemplate) bean;
        OpenTelemetry openTelemetry = openTelemetryProvider.getIfUnique();
        if (openTelemetry != null) {
            ClientHttpRequestInterceptor interceptor =
                    OrbitSpringWebTelemetry.create(openTelemetry).newInterceptor();
            addRestTemplateInterceptorIfNotPresent(restTemplate, interceptor);
        }
        return restTemplate;
    }

    private static void addRestTemplateInterceptorIfNotPresent(
            RestTemplate restTemplate, ClientHttpRequestInterceptor instrumentationInterceptor) {
        List<ClientHttpRequestInterceptor> restTemplateInterceptors = restTemplate.getInterceptors();
        if (restTemplateInterceptors.stream()
                .noneMatch(
                        interceptor -> interceptor.getClass() == instrumentationInterceptor.getClass())) {
            restTemplateInterceptors.add(0, instrumentationInterceptor);
        }
    }
}
