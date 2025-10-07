package com.cosium.matrix_communication_client;

import com.cosium.matrix_communication_client.media.Media;
import com.cosium.matrix_communication_client.media.MediaResource;
import com.cosium.matrix_communication_client.room.RoomsResource;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;

/**
 * @author Réda Housni Alaoui
 */
class SimpleMatrixResources implements MatrixResources {

  private final ObjectMapper objectMapper;
  private final AccessTokenFactory accessTokenFactory;
  private final Lazy<MatrixApi> api;
  private final Lazy<MediaResource> media;

  public SimpleMatrixResources(
      boolean https,
      String hostname,
      Integer port,
      Duration connectTimeout,
      AccessTokenFactoryFactory accessTokenFactoryFactory) {
    objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    MatrixUris matrixUris = new MatrixUris(https, new MatrixHostname(hostname), port);
    JsonHandlers jsonHandlers = new JsonHandlers(objectMapper);
    HttpClientFactory httpClientFactory = new HttpClientFactory(connectTimeout);
    accessTokenFactory =
        accessTokenFactoryFactory.build(httpClientFactory, jsonHandlers, matrixUris);
    api =
        Lazy.of(
            () -> MatrixApi.load(httpClientFactory, jsonHandlers, matrixUris, accessTokenFactory));
  media = Lazy.of(() -> new Media(api.get()));
  }

  @Override
  public AccessTokensResource accessTokens() {
    return new SimpleAccessTokensResource(accessTokenFactory);
  }

  @Override
  public RoomsResource rooms() {
    return new SimpleRoomsResource(api, objectMapper);
  }

  @Override
  public MediaResource media() {
    return media.get();
  }
}
