package com.cosium.matrix_communication_client.room;

import com.cosium.matrix_communication_client.CreateRoomInput;

/**
 * @author Réda Housni Alaoui
 */
public interface RoomsResource {

  RoomResource byId(String id);

  RoomResource create(CreateRoomInput input);
}
