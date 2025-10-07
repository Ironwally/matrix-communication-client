package com.cosium.matrix.client.auth;

import static java.util.Objects.requireNonNull;

import com.cosium.matrix.client.core.HttpClientFactory;
import com.cosium.matrix.client.core.MatrixUnprotectedApi;
import com.cosium.matrix.client.core.MatrixUris;
import com.cosium.matrix.client.serialization.JsonHandlers;
import com.cosium.matrix.client.util.Lazy;

class UsernamePasswordAccessTokenFactoryFactory implements AccessTokenFactoryFactory {

  private final String username;
  private final String password;

  UsernamePasswordAccessTokenFactoryFactory(String username, String password) {
    this.username = requireNonNull(username);
    this.password = requireNonNull(password);
  }

  @Override
  public AccessTokenFactory build(
      HttpClientFactory httpClientFactory, JsonHandlers jsonHandlers, MatrixUris uris) {
    return new UsernamePasswordAccessTokenFactory(
        Lazy.of(() -> MatrixUnprotectedApi.load(httpClientFactory, jsonHandlers, uris)),
        username,
        password);
  }
}
