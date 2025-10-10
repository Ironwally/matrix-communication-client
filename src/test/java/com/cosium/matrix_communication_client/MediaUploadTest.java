package com.cosium.matrix_communication_client;

import static org.assertj.core.api.Assertions.assertThat;

import com.cosium.matrix_communication_client.media.MediaResource;
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

  @Test
  @DisplayName("Upload an Image to the Media Repository")
  void uploadImage() {
      final File imageFile = new File("src/test/resources/cat.jpg");
      byte[] imageBytes;
      try {
          imageBytes = Files.readAllBytes(imageFile.toPath());
      } catch (final IOException e) {
          throw new RuntimeException(e);
      }

      final String contentUri = resources.media().upload(imageFile, "my pretty cat", "image/jpeg");
      assertThat(contentUri).isNotBlank();
      final byte[] downloaded = resources.media().download(contentUri);
      assertThat(downloaded).isEqualTo(imageBytes);
  }

  @Test
  @DisplayName("Upload Invalid File Type")
  void uploadInvalidFileType() {
    final byte[] bytes = new byte[] {0x41, 0x42, 0x43, 0x44}; // "ABCD"
    File textFile;
    try {
      textFile = File.createTempFile("invalid", ".txt");
      Files.write(textFile.toPath(), bytes);
      textFile.deleteOnExit();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
    final String contentUri = resources.media().upload(textFile, "not an image", "text/plain");
    assertThat(contentUri).isNotBlank();

    final byte[] downloaded = resources.media().download(contentUri);
    assertThat(downloaded).isNotEmpty();
    // TODO: Implement better check
  }

  @Test
  @DisplayName("Upload Empty File")
  void uploadEmptyFile() throws IOException {
    final File emptyFile = File.createTempFile("empty", ".tmp");
    emptyFile.deleteOnExit();

    final String contentUri = resources.media().upload(emptyFile, "empty file", "application/octet-stream");
    assertThat(contentUri).isNotBlank();
    final byte[] downloaded = resources.media().download(contentUri);
    assertThat(downloaded).isEmpty();
  }

    private RoomResource createRoom() {
        final CreateRoomInput createRoomInput = CreateRoomInput.builder()
                .name(MediaUploadTest.class.getSimpleName() + UUID.randomUUID().toString())
                .roomAliasName(MediaUploadTest.class.getSimpleName() + UUID.randomUUID().toString())
                .topic(MediaUploadTest.class.getSimpleName() + UUID.randomUUID().toString())
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

    @BeforeEach
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

    private void sleep(final Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for room events", e);
        }
    }
}
