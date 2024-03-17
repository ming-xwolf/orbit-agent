/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.web.orbit;

import io.opentelemetry.instrumentation.api.instrumenter.net.NetClientAttributesGetter;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import javax.annotation.Nullable;

final class SpringWebNetAttributesGetter
    implements NetClientAttributesGetter<HttpRequest, ClientHttpResponse> {

  @Override
  @Nullable
  public String getServerAddress(HttpRequest httpRequest) {
    return httpRequest.getURI().getHost();
  }

  @Override
  public Integer getServerPort(HttpRequest httpRequest) {
    return httpRequest.getURI().getPort();
  }
}
