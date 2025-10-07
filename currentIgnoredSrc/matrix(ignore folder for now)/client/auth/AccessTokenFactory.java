package com.cosium.matrix.client.auth;

/**
 * Builds Matrix access tokens on demand.
 */
interface AccessTokenFactory {

  String build();
}
