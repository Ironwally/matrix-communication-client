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
@SuppressWarnings("unused")
public class MediaUploadTest {

    private static final String SAMPLE_IMAGE_BASE64 = "/9j/4AAQSkZJRgABAQEASABIAAD/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH/wAALCAABAAIBASIA/8QAFQABAQAAAAAAAAAAAAAAAAAAAAf/xAAUEAEAAAAAAAAAAAAAAAAAAAAA/8QAFQEBAQAAAAAAAAAAAAAAAAAAAgP/xAAUEQEAAAAAAAAAAAAAAAAAAAAA/9oADAMBAAIRAxEAPwCfAAD/2Q==";
    private static final byte[] SAMPLE_IMAGE_BYTES = Base64.getDecoder().decode(SAMPLE_IMAGE_BASE64);
    private static final Duration EVENT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration EVENT_POLL_INTERVAL = Duration.ofMillis(200);

    private MatrixResources resources;
    private File imageFile;
    private byte[] imageBytes;

    @BeforeEach
    @SuppressWarnings("unused")
    void beforeEach(Synapse synapse) throws IOException {
        resources = MatrixResources.factory()
            .builder()
            .https(synapse.https())
            .hostname(synapse.hostname())
            .port(synapse.port())
            .usernamePassword(synapse.adminUsername(), synapse.adminPassword())
            .build();
        imageBytes = SAMPLE_IMAGE_BYTES;
        imageFile = Files.createTempFile("matrix-cat", ".jpg").toFile();
        imageFile.deleteOnExit();
        Files.write(imageFile.toPath(), imageBytes);
  }

    @Test
    @DisplayName("Send an Image to a Room")
    @SuppressWarnings("unused")
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
    @SuppressWarnings("unused")
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
                .name(UUID.randomUUID().toString())
                .roomAliasName(UUID.randomUUID().toString())
                .topic(UUID.randomUUID().toString())
                .build();
        return resources.rooms().create(createRoomInput);
    }

    @SuppressWarnings("unchecked")
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
