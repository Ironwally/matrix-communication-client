package com.cosium.matrix_communication_client;

import static org.assertj.core.api.Assertions.assertThat;

import com.cosium.matrix_communication_client.media.MediaResource;
import com.cosium.matrix_communication_client.message.Message;
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

@EnableSynapse
public class MediaUploadTest {

    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration EVENT_POLL_INTERVAL = Duration.ofMillis(200);

    private MatrixResources resources;

    @BeforeEach
    void beforeEach(Synapse synapse) throws IOException {
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

  @Test
  @DisplayName("Upload an Image to the Media Repository")
  void uploadImage() {
      File imageFile = new File("src/test/resources/cat.jpg");
      byte[] imageBytes;
      try {
          imageBytes = Files.readAllBytes(imageFile.toPath());
      } catch (IOException e) {
          throw new RuntimeException(e);
      }

      String contentUri = resources.media().upload(imageFile, "my pretty cat", "image/jpeg");
      assertThat(contentUri).isNotBlank();
      byte[] downloaded = resources.media().download(contentUri);
      assertThat(downloaded).isEqualTo(imageBytes);
  }

  @Test
  @DisplayName("Upload and send an Image to a room")
  void uploadAndSendImage() {
      RoomResource room = createRoom();
      File imageFile = new File("src/test/resources/cat.jpg");
      byte[] imageBytes;
      try {
          imageBytes = Files.readAllBytes(imageFile.toPath());
      } catch (IOException e) {
          throw new RuntimeException(e);
      }
      String contentUri = resources.media().upload(imageFile, "cat.jpg", "image/jpeg");
      Message message = Message.builder()
          .image()
          .caption("my pretty cat")
          .url(contentUri)
          .filename("cat.jpg")
          .imageInfo(600, "image/jpeg", imageBytes.length, 800)
          .build();
      room.sendMessage(message);

      List<byte[]> downloaded = awaitDownloadedImages(room, 1);
      assertThat(downloaded).hasSize(1);
      assertThat(downloaded.get(0)).isEqualTo(imageBytes);
  }

  @Test
  @DisplayName("Upload Invalid File Type")
  void uploadInvalidFileType() {
    byte[] bytes = new byte[] {0x41, 0x42, 0x43, 0x44}; // "ABCD"
    File textFile;
    try {
      textFile = File.createTempFile("invalid", ".txt");
      Files.write(textFile.toPath(), bytes);
      textFile.deleteOnExit();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String contentUri = resources.media().upload(textFile, "not an image", "text/plain");
    assertThat(contentUri).isNotBlank();

    byte[] downloaded = resources.media().download(contentUri);
    assertThat(downloaded).isNotEmpty();
    // TODO: Implement better check
  }

  @Test
  @DisplayName("Upload Empty File")
  void uploadEmptyFile() throws IOException {
    File emptyFile = File.createTempFile("empty", ".tmp");
    emptyFile.deleteOnExit();

    String contentUri = resources.media().upload(emptyFile, "empty file", "application/octet-stream");
    assertThat(contentUri).isNotBlank();
    byte[] downloaded = resources.media().download(contentUri);
    assertThat(downloaded).isEmpty();
  }

    private RoomResource createRoom() {
        CreateRoomInput createRoomInput = CreateRoomInput.builder()
                .name(MediaUploadTest.class.getSimpleName() + UUID.randomUUID().toString())
                .roomAliasName(MediaUploadTest.class.getSimpleName() + UUID.randomUUID().toString())
                .topic(MediaUploadTest.class.getSimpleName() + UUID.randomUUID().toString())
                .build();
          return resources.rooms().create(createRoomInput);
    }

    private List<byte[]> awaitDownloadedImages(RoomResource room, int expectedCount) {
        Instant deadline = Instant.now().plus(EVENT_TIMEOUT);
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

        MediaResource mediaResource = resources.media();
        return contents.stream()
            .map(content -> (String) content.get("url"))
            .peek(url -> assertThat(url).isNotBlank())
            .map(mediaResource::download)
            .toList();
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for room events", e);
        }
    }
}
