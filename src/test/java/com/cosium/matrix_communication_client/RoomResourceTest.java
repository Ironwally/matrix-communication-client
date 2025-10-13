package com.cosium.matrix_communication_client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.tuple;

import com.cosium.matrix_communication_client.message.Message;
import com.cosium.matrix_communication_client.message.MessageText;
import com.cosium.matrix_communication_client.room.RoomResource;
import com.cosium.synapse_junit_extension.EnableSynapse;
import com.cosium.synapse_junit_extension.Synapse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Réda Housni Alaoui
 */
@EnableSynapse
class RoomResourceTest {

  private MatrixResources resources;

  @BeforeEach
  void beforeEach(Synapse synapse) {
    resources =
        MatrixResources.factory()
            .builder()
            .https(synapse.https())
            .hostname(synapse.hostname())
            .port(synapse.port())
            .usernamePassword(synapse.adminUsername(), synapse.adminPassword())
            .build();
    // Overriding resources with own running matrix server to see server logs
    resources = MatrixResources.factory()
                .builder()
                .http()
                .hostname("localhost")
                .defaultPort()
                .usernamePassword("admin", "magentaerenfarve")
                .build();
  }

  @Test
  @DisplayName("Create room")
  void test1() {
    assertThatCode(this::createRoom).doesNotThrowAnyException();
  }

  @Test
  @DisplayName("Send message to room")
  void test2() {
    MessageText message = MessageText.builder().text("body").build();
    Message fetchedMessage = createRoom().sendMessage(message).fetch().content(Message.class);

    assertThat(List.of(fetchedMessage))
        .extracting(Message::body, Message::format, Message::formattedBody, Message::type)
        .containsExactly(
            tuple(message.body(), message.format(), message.formattedBody(), message.type()));
  }

  @Test
  @DisplayName("Fetch event page")
  void test3() {
    MessageText message = MessageText.builder().text("Fetch event page test").build();
    RoomResource room = createRoom();
    room.sendMessage(message);

    ClientEventPage eventPage = room.fetchEventPage(null, null, null, null);
    List<ClientEvent> fetchedEvents = eventPage.chunk();
    assertThat(fetchedEvents)
        .filteredOn(clientEvent -> "m.room.message".equals(clientEvent.type()))
        .map(clientEvent -> clientEvent.content(Message.class))
        .extracting(Message::body, Message::format, Message::formattedBody, Message::type)
        .containsExactly(
            tuple(message.body(), message.format(), message.formattedBody(), message.type()));
  }

  private RoomResource createRoom() {
    CreateRoomInput createRoomInput =
        CreateRoomInput.builder()
            .name(RoomResourceTest.class.getSimpleName() + UUID.randomUUID().toString())
            .roomAliasName(RoomResourceTest.class.getSimpleName() + UUID.randomUUID().toString())
            .topic(RoomResourceTest.class.getSimpleName() + UUID.randomUUID().toString())
            .build();
    return resources.rooms().create(createRoomInput);
  }
}
