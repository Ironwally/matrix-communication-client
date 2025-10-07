package com.cosium.matrix_communication_client.room;

import com.cosium.matrix_communication_client.ClientEventPage;
import com.cosium.matrix_communication_client.ClientEventResource;
import com.cosium.matrix_communication_client.media.AttachmentConfig;
import com.cosium.matrix_communication_client.message.Message;
import java.io.File;

/**
 * @author Réda Housni Alaoui
 */
public interface RoomResource {

  String id();

  /**
   * <a
   * href="https://spec.matrix.org/latest/client-server-api/#mroommessage">https://spec.matrix.org/latest/client-server-api/#mroommessage</a>
   */
  ClientEventResource sendMessage(Message messageInput);

  /**
   * @param dir The direction to return events from. If this is set to f, events will be returned in
   *     chronological order starting at from. If it is set to b, events will be returned in reverse
   *     chronological order, again starting at from. One of: [b, f].
   * @param from The token to start returning events from. This token can be obtained from a
   *     prev_batch or next_batch token returned by the /sync endpoint, or from an end token
   *     returned by a previous request to this endpoint.
   *     <p>This endpoint can also accept a value returned as a start token by a previous request to
   *     this endpoint, though servers are not required to support this. Clients should not rely on
   *     the behaviour.
   *     <p>If it is not provided, the homeserver shall return a list of messages from the first or
   *     last (per the value of the dir parameter) visible event in the room history for the
   *     requesting user.
   * @param limit The maximum number of events to return.
   * @param to The token to stop returning events at. This token can be obtained from a prev_batch
   *     or next_batch token returned by the /sync endpoint, or from an end token returned by a
   *     previous request to this endpoint.
   * @return A page of events
   */
  ClientEventPage fetchEventPage(String dir, String from, Long limit, String to);

  void sendAttachment(File file, AttachmentConfig config);
  void sendAttachment(String filename, String contentType, File file, AttachmentConfig config);
  /**
   * Sends an attachment to the room. This is a convenience method that uploads the attachment to
   * the media repository and sends a message with the resulting mxc:// URI.
   *
   * Performs: TODO
   * Upload (and optional thumbnail upload)
   * Constructs the proper Image message content from the returned MXC URI(s)
   * Sends the resulting m.room.message event
   *
   * @param filename The file name.
   * @param contentType The type of the media, this will be used as the content-type header.
   * @param data
   * @param config Metadata and configuration for the attachment.
   */
  void sendAttachment(String filename, String contentType, byte[] data, AttachmentConfig config);
}
