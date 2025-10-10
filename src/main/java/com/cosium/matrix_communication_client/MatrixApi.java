package com.cosium.matrix_communication_client;

import static java.util.Objects.requireNonNull;

import com.cosium.matrix_communication_client.message.Message;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Réda Housni Alaoui
 */
public class MatrixApi {

  private static final String APPLICATION_JSON = "application/json";
  private final HttpClient httpClient;
  private final JsonHandlers jsonHandlers;
  private final MatrixUri baseUri;
  private final MatrixUri mediaUri;
  private final AccessTokenFactory accessTokenFactory;

  private MatrixApi(
      final HttpClientFactory httpClientFactory,
      final JsonHandlers jsonHandlers,
      final MatrixUris uris,
      final AccessTokenFactory accessTokenFactory) {
    httpClient = httpClientFactory.build();
    this.jsonHandlers = requireNonNull(jsonHandlers);
    baseUri = uris.fetchBaseUri(httpClient, jsonHandlers);
    mediaUri = baseUri.withPathSegments("_matrix", "media", "v3");
    this.accessTokenFactory = requireNonNull(accessTokenFactory);
  }

  public static MatrixApi load(
      final HttpClientFactory httpClientFactory,
      final JsonHandlers jsonHandlers,
      final MatrixUris uris,
      final AccessTokenFactory accessTokenFactory) {
    return new MatrixApi(httpClientFactory, jsonHandlers, uris, accessTokenFactory);
  }

  public CreatedEvent sendMessageToRoom(final Message input, final String roomId) {

    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(
                baseUri
                    .addPathSegments(
                        "rooms", roomId, "send", "m.room.message", UUID.randomUUID().toString())
                    .toUri())
            .header("Authorization", String.format("Bearer %s", accessTokenFactory.build()))
            .header("Content-Type", APPLICATION_JSON)
            .header("Accept", APPLICATION_JSON)
            .PUT(jsonHandlers.publisher(input))
            .build();

    try {
      return httpClient
          .send(request, jsonHandlers.handler(CreatedEvent.class))
          .body()
          .get()
          .parse();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  // --- helpers

  public UploadMediaEvent uploadMedia(final String filename, final String contentType, final byte[] data) {
    return uploadMedia(filename, contentType, BodyPublishers.ofByteArray(data));
  }

  public UploadMediaEvent uploadMedia(final String filename, final String contentType, final HttpRequest.BodyPublisher body) {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(mediaUri
                .addPathSegments("upload")
                .addQueryParameter("filename", filename)
                .toUri())
            .header("Authorization", String.format("Bearer %s", accessTokenFactory.build()))
            .header("Content-Type", contentType)
            .header("Accept", APPLICATION_JSON)
            .POST(body)
            .build();

    try {
      return httpClient.send(request, jsonHandlers.handler(UploadMediaEvent.class))
          .body()
          .get()
          .parse();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  public byte[] downloadMedia(final String mxcUri) {
    final URI uri = URI.create(mxcUri);
    if (!"mxc".equalsIgnoreCase(uri.getScheme())) {
      throw new IllegalArgumentException("URI must use the mxc scheme");
    }

    final String serverName = Optional.ofNullable(uri.getHost())
        .filter(host -> !host.isBlank())
        .orElseThrow(() -> new IllegalArgumentException("mxc URI must contain a server name"));

    final String rawPath = Optional.ofNullable(uri.getPath())
        .filter(path -> !path.isBlank())
        .orElseThrow(() -> new IllegalArgumentException("mxc URI must contain a media identifier"));

    final String mediaId = rawPath.startsWith("/") ? rawPath.substring(1) : rawPath;

    MatrixUri downloadUri = mediaUri.addPathSegments("download", serverName);
    for (final String segment : mediaId.split("/")) {
      if (!segment.isBlank()) {
        downloadUri = downloadUri.addPathSegments(segment);
      }
    }

    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(downloadUri.toUri())
            .header("Authorization", String.format("Bearer %s", accessTokenFactory.build()))
            .header("Accept", "application/octet-stream")
            .GET()
            .build();

    try {
      return httpClient.send(request, BodyHandlers.ofByteArray()).body();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }



  public CreateRoomOutput createRoom(final CreateRoomInput input) {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(baseUri.addPathSegments("createRoom").toUri())
            .header("Authorization", String.format("Bearer %s", accessTokenFactory.build()))
            .header("Content-Type", APPLICATION_JSON)
            .header("Accept", APPLICATION_JSON)
            .POST(jsonHandlers.publisher(input))
            .build();

    try {
      return httpClient
          .send(request, jsonHandlers.handler(CreateRoomOutput.class))
          .body()
          .get()
          .parse();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  public RawClientEvent fetchEvent(final String roomId, final String eventId) {
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(baseUri.addPathSegments("rooms", roomId, "event", eventId).toUri())
            .header("Authorization", String.format("Bearer %s", accessTokenFactory.build()))
            .header("Accept", APPLICATION_JSON)
            .GET()
            .build();

    try {
      return httpClient
          .send(request, jsonHandlers.handler(RawClientEvent.class))
          .body()
          .get()
          .parse();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }

  /*
   * Matrix Spec Link: https://github.com/element-hq/synapse/blob/develop/docs/admin_api/rooms.md#Room-messages-api
   */
  public RawClientEventPage fetchMessagePage(
      final String roomId, final String dir, final String from, final String limit, final String to) {
    final URI uri =
        baseUri
            .addPathSegments("rooms", roomId, "messages")
            .addQueryParameter("dir", dir)
            .addQueryParameter("from", from)
            .addQueryParameter("limit", limit)
            .addQueryParameter("to", to)
            .toUri();
    final HttpRequest request =
        HttpRequest.newBuilder()
            .uri(uri)
            .header("Authorization", String.format("Bearer %s", accessTokenFactory.build()))
            .header("Accept", APPLICATION_JSON)
            .GET()
            .build();

    try {
      return httpClient
          .send(request, jsonHandlers.handler(RawClientEventPage.class))
          .body()
          .get()
          .parse();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    }
  }
}
