package com.cosium.matrix.client.core;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Optional;

class HttpClientFactory {

  private final Duration connectTimeout;

  HttpClientFactory(Duration connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  HttpClient build() {
    HttpClient.Builder builder = HttpClient.newBuilder();
    Optional.ofNullable(connectTimeout).ifPresent(builder::connectTimeout);
    return builder.followRedirects(HttpClient.Redirect.NORMAL).build();
  }
}
