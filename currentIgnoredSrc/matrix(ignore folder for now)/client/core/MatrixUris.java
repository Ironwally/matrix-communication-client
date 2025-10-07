package com.cosium.matrix.client.core;

import static java.util.Objects.requireNonNull;

import com.cosium.matrix.client.serialization.JsonHandlers;
import java.net.http.HttpClient;

class MatrixUris {

  private final boolean https;
  private final MatrixHostname hostname;
  private final Integer port;

  MatrixUris(boolean https, MatrixHostname hostname, Integer port) {
    this.https = https;
    this.hostname = requireNonNull(hostname);
    this.port = port;
  }

  MatrixUri create(String... paths) {
    return hostname.createUri(https, port, paths);
  }

  MatrixUri fetchBaseUri(HttpClient httpClient, JsonHandlers jsonHandlers) {
    return WellKnownMatrixClient.fetch(httpClient, jsonHandlers, this)
        .map(WellKnownMatrixClient::homeServer)
        .map(WellKnownMatrixClient.HomeServer::baseUri)
        .orElseGet(this::create)
        .addPathSegments("_matrix", "client", "v3");
  }
}
