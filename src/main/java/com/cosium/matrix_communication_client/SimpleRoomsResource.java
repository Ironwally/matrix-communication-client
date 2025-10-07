package com.cosium.matrix_communication_client;

import static java.util.Objects.requireNonNull;

import com.cosium.matrix_communication_client.room.RoomResource;
import com.cosium.matrix_communication_client.room.RoomsResource;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Réda Housni Alaoui
 */
public class SimpleRoomsResource implements RoomsResource {

  private final Lazy<MatrixApi> api;
  private final ObjectMapper objectMapper;

  SimpleRoomsResource(Lazy<MatrixApi> api, ObjectMapper objectMapper) {
    this.api = requireNonNull(api);
    this.objectMapper = requireNonNull(objectMapper);
  }

  @Override
  public RoomResource byId(String id) {
    return new SimpleRoomResource(api, objectMapper, id);
  }

  @Override
  public RoomResource create(CreateRoomInput input) {
    return new SimpleRoomResource(api, objectMapper, api.get().createRoom(input).roomId());
  }
}
