package com.cosium.matrix_communication_client.room;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

import com.cosium.matrix_communication_client.ClientEventPage;
import com.cosium.matrix_communication_client.ClientEventResource;
import com.cosium.matrix_communication_client.CreatedEvent;
import com.cosium.matrix_communication_client.Lazy;
import com.cosium.matrix_communication_client.MatrixApi;
import com.cosium.matrix_communication_client.RawClientEventPage;
import com.cosium.matrix_communication_client.SimpleClientEventResource;
import com.cosium.matrix_communication_client.media.AttachmentConfig;
import com.cosium.matrix_communication_client.message.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import org.springframework.http.MediaTypeFactory;

/**
 * @author Réda Housni Alaoui
 */
class NewRoomResource implements RoomResource {

  private final Lazy<MatrixApi> api;
  private final ObjectMapper objectMapper;
  private final String id;

  public NewRoomResource(Lazy<MatrixApi> api, ObjectMapper objectMapper, String id) {
    this.api = requireNonNull(api);
    this.objectMapper = requireNonNull(objectMapper);
    this.id = requireNonNull(id);
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public ClientEventResource sendMessage(Message message) {
    CreatedEvent createdEvent = api.get().sendMessageToRoom(message, id);
    return new SimpleClientEventResource(api, objectMapper, id, createdEvent.id());
  }

  @Override
  public ClientEventPage fetchEventPage(String dir, String from, Long limit, String to) {
    RawClientEventPage raw =
        api.get()
            .fetchMessagePage(
                id, dir, from, ofNullable(limit).map(String::valueOf).orElse(null), to);
    return new ClientEventPage(objectMapper, raw);
  }

  @Override
  public void sendAttachment(File file, AttachmentConfig config) {
    MediaTypeFactory.getMediaType(file.getName()).ifPresentOrElse(
        mediaType -> sendAttachment(file.getName(), mediaType.toString(), file, config),
        () -> sendAttachment(file.getName(), null, file, config));
  }
  @Override
  public void sendAttachment(String filename, String contentType, File file, AttachmentConfig config) {
    api.get().sendImageAttachmentToRoom(id, filename, contentType, file.toPath(), config);
  }
  @Override
  public void sendAttachment(String filename, String contentType, byte[] data, AttachmentConfig config) {
    api.get().sendImageAttachmentToRoom(id, filename, contentType, data, config);
  }
}

