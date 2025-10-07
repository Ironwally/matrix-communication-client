package com.cosium.matrix_communication_client;

import static org.assertj.core.api.Assertions.assertThat;

import com.cosium.matrix_communication_client.media.AttachmentConfig;
import com.cosium.matrix_communication_client.media.MediaResource;
import com.cosium.matrix_communication_client.room.RoomResource;
import com.cosium.synapse_junit_extension.EnableSynapse;
import com.cosium.synapse_junit_extension.Synapse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@EnableSynapse
public class MediaUploadTest {

    private static final String SAMPLE_IMAGE_BASE64 = "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";
    private static final byte[] SAMPLE_IMAGE_BYTES = Base64.getDecoder().decode(SAMPLE_IMAGE_BASE64);
    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration EVENT_POLL_INTERVAL = Duration.ofMillis(200);

    private MatrixResources resources;
    private File imageFile;
    private byte[] imageBytes;

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
        imageBytes = SAMPLE_IMAGE_BYTES;
        imageFile = Files.createTempFile("matrix-cat", ".jpg").toFile();
        imageFile.deleteOnExit();
        Files.write(imageFile.toPath(), imageBytes);
  }

    @Test
    @DisplayName("Send an Image to a Room")
    void sendAttachment() {
        RoomResource room = createRoom();
        AttachmentConfig attachmentConfig = AttachmentConfig.builder()
            .caption("my pretty cat")
            .contentType("image/jpeg")
            .build();
        room.sendAttachment(imageFile, attachmentConfig);
        room.sendAttachment(
            imageFile.getName(),
            "image/jpeg",
            imageFile,
            AttachmentConfig.builder()
                .caption("my pretty cat")
                .contentType("image/jpeg")
                .build()
        );
        List<byte[]> downloadedImages = awaitDownloadedImages(room, 2);
        assertThat(downloadedImages)
            .hasSizeGreaterThanOrEqualTo(2)
            .allSatisfy(bytes -> assertThat(bytes).isEqualTo(imageBytes));
    }

    @Test
    @DisplayName("Upload an Image to the Media Repository")
    void uploadImage() {
        AttachmentConfig attachmentConfig = AttachmentConfig.builder()
            .caption("my pretty cat")
            .contentType("image/jpeg")
            .build();
        String contentUri = resources.media().upload(imageFile, attachmentConfig);
        assertThat(contentUri).isNotBlank();

        byte[] downloaded = resources.media().download(contentUri);
        assertThat(downloaded).isEqualTo(imageBytes);
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

        // TODO: URL not present in content, how to get it?
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
