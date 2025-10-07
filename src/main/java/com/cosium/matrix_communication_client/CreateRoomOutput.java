package com.cosium.matrix_communication_client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Réda Housni Alaoui
 * Returned by MatrixApi after sending Room creation request
 */
public class CreateRoomOutput {

  private final String roomId;

  @JsonCreator
  CreateRoomOutput(@JsonProperty("room_id") String roomId) {
    this.roomId = roomId;
  }

  public String roomId() {
    return roomId;
  }
}
