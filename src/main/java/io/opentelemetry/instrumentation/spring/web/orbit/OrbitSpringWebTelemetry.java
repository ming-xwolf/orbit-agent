package io.opentelemetry.instrumentation.spring.web.orbit;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

public class OrbitSpringWebTelemetry {

    public static OrbitSpringWebTelemetry create(OpenTelemetry openTelemetry) {
        return builder(openTelemetry).build();
    }

    public static OrbitSpringWebTelemetryBuilder builder(OpenTelemetry openTelemetry) {
        return new OrbitSpringWebTelemetryBuilder(openTelemetry);
    }

    private final Instrumenter<HttpRequest, ClientHttpResponse> instrumenter;

    OrbitSpringWebTelemetry(Instrumenter<HttpRequest, ClientHttpResponse> instrumenter) {
        this.instrumenter = instrumenter;
    }

    /**
     * Returns a new {@link ClientHttpRequestInterceptor} that can be used with {@link
     * RestTemplate#getInterceptors()}. For example:
     *
     * <pre>{@code
     * restTemplate.getInterceptors().add(SpringWebTelemetry.create(openTelemetry).newInterceptor());
     * }</pre>
     */
    public ClientHttpRequestInterceptor newInterceptor() {
        return new OrbitRestTemplateInterceptor(instrumenter);
    }
}
