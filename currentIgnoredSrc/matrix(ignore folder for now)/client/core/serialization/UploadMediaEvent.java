package com.cosium.matrix.client.core.serialization;

import static java.util.Objects.requireNonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the successful response body returned by {@code POST /_matrix/media/v3/upload}.
 *
 * <p>See <a href="https://spec.matrix.org/v1.16/client-server-api/#post_matrixmediav3upload">Matrix
 * Client-Server API - Upload content</a>.</p>
 */
@SuppressWarnings("unused")
public class UploadMediaEvent {

  private final String contentUri;

  @JsonCreator
  public UploadMediaEvent(@JsonProperty(value = "content_uri", required = true) String contentUri) {
    this.contentUri = requireNonNull(contentUri, "contentUri");
  }

  /**
   * The MXC URI referencing the uploaded media.
   */
  public String contentUri() {
    return contentUri;
  }
}
