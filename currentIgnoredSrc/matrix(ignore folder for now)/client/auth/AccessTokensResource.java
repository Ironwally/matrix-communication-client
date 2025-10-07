package com.cosium.matrix.client.auth;

/**
 * Exposes operations to obtain Matrix access tokens.
 */
public interface AccessTokensResource {

  /**
   * @return an access token
   */
  String create();
}
