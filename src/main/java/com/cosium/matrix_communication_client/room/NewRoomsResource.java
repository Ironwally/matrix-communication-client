package com.cosium.matrix_communication_client.room;

import static java.util.Objects.requireNonNull;

import com.cosium.matrix_communication_client.CreateRoomInput;
import com.cosium.matrix_communication_client.Lazy;
import com.cosium.matrix_communication_client.MatrixApi;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Réda Housni Alaoui
 */
public class NewRoomsResource implements RoomsResource {

  private final Lazy<MatrixApi> api;
  private final ObjectMapper objectMapper;

  public NewRoomsResource(Lazy<MatrixApi> api, ObjectMapper objectMapper) {
    this.api = requireNonNull(api);
    this.objectMapper = requireNonNull(objectMapper);
  }

  @Override
  public RoomResource byId(String id) {
    return new NewRoomResource(api, objectMapper, id);
  }

  @Override
  public RoomResource create(CreateRoomInput input) {
    return new NewRoomResource(api, objectMapper, api.get().createRoom(input).roomId());
  }
}
