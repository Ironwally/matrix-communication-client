package com.cosium.matrix_communication_client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.cosium.matrix_communication_client.media.MediaResource;
import com.cosium.matrix_communication_client.message.Message;
import com.cosium.matrix_communication_client.message.MessageFile;
import com.cosium.matrix_communication_client.message.MessageImage;
import com.cosium.matrix_communication_client.message.MessageText;
import com.cosium.matrix_communication_client.room.RoomResource;
import com.cosium.synapse_junit_extension.EnableSynapse;
import com.cosium.synapse_junit_extension.Synapse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

@EnableSynapse
public class MessageTest {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration EVENT_POLL_INTERVAL = Duration.ofMillis(200);
    private MatrixResources resources;

  @Test
  @DisplayName("Send MessageText to a room")
  void sendMessageText() {
      final RoomResource room = createRoom();
      final String messageText = "MessageTest text";
      final MessageText message = MessageText.builder()
          .text(messageText)
          .build();
      final ClientEventResource event = room.sendMessage(message);
      final Message fetchedMessage = event.fetch().content(Message.class);

      assertThat(List.of(fetchedMessage))
        .extracting(Message::body, Message::format, Message::formattedBody, Message::type)
        .containsExactly(
            tuple(message.body(), message.format(), message.formattedBody(), message.type()));
  }

  @Test
  @DisplayName("Send MessageImage to a room")
  void sendMessageImage() {
      final RoomResource room = createRoom();
      final File imageFile = new File("src/test/resources/cat.jpg");
      byte[] imageBytes;
      try {
          imageBytes = Files.readAllBytes(imageFile.toPath());
      } catch (final IOException e) {
          throw new RuntimeException(e);
      }

      final String mimeType = MediaTypeFactory.getMediaType(imageFile.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
      final String contentUri = resources.media().upload(imageFile, "cat.jpg", mimeType);
      final Message message = MessageImage.builder()
          .caption("my pretty cat")
          .url(contentUri)
          .originalFilename("cat.jpg")
          .imageInfo(600, mimeType, imageBytes.length, 800)
          .build();
      room.sendMessage(message);

      final List<byte[]> downloaded = awaitDownloadedImages(room, 1);
      assertThat(downloaded).hasSize(1);
      assertThat(downloaded.get(0)).isEqualTo(imageBytes);
  }

  @Test
  @DisplayName("Send MessageFile to a room")
  void sendMessageFile() {
      final RoomResource room = createRoom();
      final MediaResource media = resources.media();

      final File file = new File("src/test/resources/cat.pdf");

      final String mimeType = MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
      final String contentUri = media.upload(file, "cat.pdf", mimeType);
      final String messageText = "Caption of cat file attachment";
      final MessageFile message = MessageFile.builder()
          .caption(messageText)
          .url(contentUri)
          .originalFilename("cat.pdf")
          .fileInfo(mimeType, file.length())
          .build();
      room.sendMessage(message);

      final ClientEventPage eventpage = room.fetchEventPage("b", null, 10L, null);
      eventpage.chunk()
        .stream()
        .filter(clientEvent -> "m.room.message".equals(clientEvent.type()))
        .filter(clientEvent -> "m.file".equals(clientEvent.type()))
        .map(clientEvent -> clientEvent.content(MessageFile.class))
        .forEach(fetchedMessage -> {
            assertThat(List.of(fetchedMessage))
                .extracting(MessageFile::body, MessageFile::format, MessageFile::formattedBody, MessageFile::type, MessageFile::url, MessageFile::filename, MessageFile::info)
                .containsExactly(
                    tuple(message.body(), message.format(), message.formattedBody(), message.type(), message.url(), message.filename(), message.info()));
        });
  }






  // --- HELPERS --- //

    @BeforeEach
    @SuppressWarnings("unused")
    void beforeEach(final Synapse synapse) throws IOException {
        resources = MatrixResources.factory()
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

    private RoomResource createRoom() {
        final CreateRoomInput createRoomInput = CreateRoomInput.builder()
                .name(MessageTest.class.getSimpleName() + UUID.randomUUID().toString())
                .roomAliasName(MessageTest.class.getSimpleName() + UUID.randomUUID().toString())
                .topic(MessageTest.class.getSimpleName() + UUID.randomUUID().toString())
                .build();
          return resources.rooms().create(createRoomInput);
    }

    private List<byte[]> awaitDownloadedImages(final RoomResource room, final int expectedCount) {
        final Instant deadline = Instant.now().plus(EVENT_TIMEOUT);
        List<Map<String, Object>> contents = List.of();
        do {
            contents = room.fetchEventPage("b", null, 10L, null)
                .chunk()
                .stream()
                .filter(clientEvent -> "m.room.message".equals(clientEvent.type()))
                .map(clientEvent -> (Map<String, Object>) clientEvent.content(Map.class))
                .filter(content -> "m.image".equals(content.get("msgtype")))
                .toList();
            if (contents.size() >= expectedCount) {
                break;
            }
            sleep(EVENT_POLL_INTERVAL);
        } while (Instant.now().isBefore(deadline));

        assertThat(contents)
            .as("image message events")
            .hasSizeGreaterThanOrEqualTo(expectedCount);

        final MediaResource mediaResource = resources.media();
        return contents.stream()
            .map(content -> (String) content.get("url"))
            .peek(url -> assertThat(url).isNotBlank())
            .map(mediaResource::download)
            .toList();
    }

    private void sleep(final Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for room events", e);
        }
    }
}
