package com.cosium.matrix.client.auth;

import static java.util.Objects.requireNonNull;

class SimpleAccessTokensResource implements AccessTokensResource {

  private final AccessTokenFactory accessTokenFactory;

  SimpleAccessTokensResource(AccessTokenFactory accessTokenFactory) {
    this.accessTokenFactory = requireNonNull(accessTokenFactory);
  }

  @Override
  public String create() {
    return accessTokenFactory.build();
  }
}
