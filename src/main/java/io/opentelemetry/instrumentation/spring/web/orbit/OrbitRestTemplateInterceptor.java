package io.opentelemetry.instrumentation.spring.web.orbit;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class OrbitRestTemplateInterceptor implements ClientHttpRequestInterceptor {
    private final Instrumenter<HttpRequest, ClientHttpResponse> instrumenter;

    OrbitRestTemplateInterceptor(Instrumenter<HttpRequest, ClientHttpResponse> instrumenter) {
        this.instrumenter = instrumenter;
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        Context parentContext = Context.current();
        if (!instrumenter.shouldStart(parentContext, request)) {
            return execution.execute(request, body);
        }

        Context context = instrumenter.start(parentContext, request);
        try (Scope ignored = context.makeCurrent()) {
            ClientHttpResponse response = execution.execute(request, body);
            instrumenter.end(context, request, response, null);
            return response;
        } catch (Throwable t) {
            instrumenter.end(context, request, null, t);
            throw t;
        }
    }
}
