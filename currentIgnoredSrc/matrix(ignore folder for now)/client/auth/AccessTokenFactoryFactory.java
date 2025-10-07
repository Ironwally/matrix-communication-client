package com.cosium.matrix.client.auth;

import com.cosium.matrix.client.core.HttpClientFactory;
import com.cosium.matrix.client.core.MatrixUris;
import com.cosium.matrix.client.serialization.JsonHandlers;

interface AccessTokenFactoryFactory {

  AccessTokenFactory build(
      HttpClientFactory httpClientFactory, JsonHandlers jsonHandlers, MatrixUris uris);
}
