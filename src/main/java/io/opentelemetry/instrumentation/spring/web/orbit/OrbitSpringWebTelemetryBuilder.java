/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.web.orbit;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.http.*;
import io.opentelemetry.instrumentation.spring.web.v3_1.SpringWebTelemetry;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.util.ArrayList;
import java.util.List;

/** A builder of {@link OrbitSpringWebTelemetryBuilder}. */

public final class OrbitSpringWebTelemetryBuilder {
  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.spring-web.orbit";

  private final OpenTelemetry openTelemetry;
  private final List<AttributesExtractor<HttpRequest, ClientHttpResponse>> additionalExtractors =
      new ArrayList<>();
  private final HttpClientAttributesExtractorBuilder<HttpRequest, ClientHttpResponse>
      httpAttributesExtractorBuilder =
          HttpClientAttributesExtractor.builder(
              io.opentelemetry.instrumentation.spring.web.orbit.SpringWebHttpAttributesGetter.INSTANCE, new io.opentelemetry.instrumentation.spring.web.orbit.SpringWebNetAttributesGetter());

  OrbitSpringWebTelemetryBuilder(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  /**
   * Adds an additional {@link AttributesExtractor} to invoke to set attributes to instrumented
   * items.
   */
  @CanIgnoreReturnValue
  public OrbitSpringWebTelemetryBuilder addAttributesExtractor(
      AttributesExtractor<HttpRequest, ClientHttpResponse> attributesExtractor) {
    additionalExtractors.add(attributesExtractor);
    return this;
  }

  /**
   * Configures the HTTP request headers that will be captured as span attributes.
   *
   * @param requestHeaders A list of HTTP header names.
   */
  @CanIgnoreReturnValue
  public OrbitSpringWebTelemetryBuilder setCapturedRequestHeaders(List<String> requestHeaders) {
    httpAttributesExtractorBuilder.setCapturedRequestHeaders(requestHeaders);
    return this;
  }

  /**
   * Configures the HTTP response headers that will be captured as span attributes.
   *
   * @param responseHeaders A list of HTTP header names.
   */
  @CanIgnoreReturnValue
  public OrbitSpringWebTelemetryBuilder setCapturedResponseHeaders(List<String> responseHeaders) {
    httpAttributesExtractorBuilder.setCapturedResponseHeaders(responseHeaders);
    return this;
  }

  /**
   * Returns a new {@link SpringWebTelemetry} with the settings of this {@link
   * OrbitSpringWebTelemetryBuilder}.
   */
  public OrbitSpringWebTelemetry build() {
    io.opentelemetry.instrumentation.spring.web.orbit.SpringWebHttpAttributesGetter httpAttributeGetter = io.opentelemetry.instrumentation.spring.web.orbit.SpringWebHttpAttributesGetter.INSTANCE;

    Instrumenter<HttpRequest, ClientHttpResponse> instrumenter =
        Instrumenter.<HttpRequest, ClientHttpResponse>builder(
                openTelemetry,
                INSTRUMENTATION_NAME,
                HttpSpanNameExtractor.create(httpAttributeGetter))
            .setSpanStatusExtractor(HttpSpanStatusExtractor.create(httpAttributeGetter))
            .addAttributesExtractor(httpAttributesExtractorBuilder.build())
            .addAttributesExtractors(additionalExtractors)
            .addOperationMetrics(HttpClientMetrics.get())
            .buildClientInstrumenter(io.opentelemetry.instrumentation.spring.web.orbit.HttpRequestSetter.INSTANCE);

    return new OrbitSpringWebTelemetry(instrumenter);
  }
}
