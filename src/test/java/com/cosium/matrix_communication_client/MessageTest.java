package com.cosium.matrix_communication_client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.cosium.matrix_communication_client.media.MediaResource;
import com.cosium.matrix_communication_client.message.Message;
import com.cosium.matrix_communication_client.message.MessageAudio;
import com.cosium.matrix_communication_client.message.MessageEmote;
import com.cosium.matrix_communication_client.message.MessageFile;
import com.cosium.matrix_communication_client.message.MessageImage;
import com.cosium.matrix_communication_client.message.MessageText;
import com.cosium.matrix_communication_client.message.MessageVideo;
import com.cosium.matrix_communication_client.room.RoomResource;
import com.cosium.synapse_junit_extension.EnableSynapse;
import com.cosium.synapse_junit_extension.Synapse;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.mp4.Mp4Directory;
import com.drew.metadata.mp4.media.Mp4VideoDirectory;
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
    private String testUUID;

  @Test
  @DisplayName("Send MessageText to a room")
  void sendMessageText() {
      final RoomResource room = createRoom("sendMessageText");
      final String messageText = "Cat\n (=^･ω･^=)";
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

  // TODO: Figure out how to use emotes
  @Test
  @DisplayName("Send MessageEmote to a room")
  void sendMessageEmote() {
      final RoomResource room = createRoom("sendMessageEmote");
      final String action = "tableflip";
      final MessageEmote message = MessageEmote.builder()
          .action(action)
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
      final RoomResource room = createRoom("sendMessageImage");
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
      final RoomResource room = createRoom("sendMessageFile");
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

  @Test
  @DisplayName("Send MessageAudio to a room")
  void sendMessageAudio() {
      final RoomResource room = createRoom("sendMessageAudio");
      final MediaResource media = resources.media();
      final File file = new File("src/test/resources/cat.mp3");
      final String mimeType = MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
      final String contentUri = media.upload(file, "cat.mp3", mimeType);
      final String messageText = "Caption of cat audio attachment";
      final MessageAudio message = MessageAudio.builder()
          .caption(messageText)
          .url(contentUri)
          .originalFilename("cat.mp3")
          .audioInfo(600, mimeType, file.length())
          .build();

      room.sendMessage(message);

      final ClientEventPage eventpage = room.fetchEventPage("b", null, 10L, null);
      eventpage.chunk()
        .stream()
        .filter(clientEvent -> "m.room.message".equals(clientEvent.type()))
        .filter(clientEvent -> "m.audio".equals(clientEvent.type()))
        .map(clientEvent -> clientEvent.content(MessageAudio.class))
        .forEach(fetchedMessage -> {
            System.out.println(fetchedMessage);
            assertThat(List.of(fetchedMessage))
                .extracting(MessageAudio::body, MessageAudio::format, MessageAudio::formattedBody, MessageAudio::type, MessageAudio::url, MessageAudio::filename, MessageAudio::info)
                .containsExactly(
                    tuple(message.body(), message.format(), message.formattedBody(), message.type(), message.url(), message.filename(), message.info()));
        });
  }

  @Test
  @DisplayName("Send MessageVideo to a room")
  void sendMessageVideo() {
      final RoomResource room = createRoom("sendMessageVideo");
      final MediaResource media = resources.media();
      final File file = new File("src/test/resources/cat.mp4");
      final String mimeType = MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
      final String contentUri = media.upload(file, "cat.mp4", mimeType);
      final String messageText = "Caption of cat video attachment";
      // Extract duration (ms), height, and width from the MP4 using helper
      final VideoMeta meta = readVideoMeta(file);
      final MessageVideo message = MessageVideo.builder()
          .caption(messageText)
          .url(contentUri)
          .originalFilename("cat.mp4")
          .videoInfo(meta.durationMs(), meta.height(), mimeType,
              (int) Math.min(Integer.MAX_VALUE, Math.max(0L, file.length())), meta.width())
          .build();

      room.sendMessage(message);

      final ClientEventPage eventpage = room.fetchEventPage("b", null, 10L, null);
      eventpage.chunk()
        .stream()
        .filter(clientEvent -> "m.room.message".equals(clientEvent.type()))
        .filter(clientEvent -> "m.video".equals(clientEvent.type()))
        .map(clientEvent -> clientEvent.content(MessageVideo.class))
        .forEach(fetchedMessage -> {
            System.out.println(fetchedMessage);
            assertThat(List.of(fetchedMessage))
                .extracting(MessageVideo::body, MessageVideo::format, MessageVideo::formattedBody, MessageVideo::type, MessageVideo::url, MessageVideo::filename, MessageVideo::info)
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
         /*
        resources = MatrixResources.factory()
                .builder()
                .http()
                .hostname("localhost")
                .defaultPort()
                .usernamePassword("admin", "magentaerenfarve")
                .build();
                */
        testUUID = UUID.randomUUID().toString();
  }

    private RoomResource createRoom(final String name) {
        final CreateRoomInput createRoomInput = CreateRoomInput.builder()
                .name(name + testUUID)
                .roomAliasName(name + testUUID)
                .topic(name + "\n" + MessageTest.class.getSimpleName() + testUUID)
                .build();
          return resources.rooms().create(createRoomInput);
    }

    @SuppressWarnings("unchecked")
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

    // --- Video metadata helper --- //
    private static VideoMeta readVideoMeta(final File file) {
        try {
            final Metadata md = ImageMetadataReader.readMetadata(file);
            final Mp4Directory mv = md.getFirstDirectoryOfType(Mp4Directory.class);
            final Mp4VideoDirectory vd = md.getFirstDirectoryOfType(Mp4VideoDirectory.class);
            final Double durSec = (mv != null) ? mv.getDoubleObject(Mp4Directory.TAG_DURATION) : null;
            final Integer wVal = (vd != null) ? vd.getInteger(Mp4VideoDirectory.TAG_WIDTH) : null;
            final Integer hVal = (vd != null) ? vd.getInteger(Mp4VideoDirectory.TAG_HEIGHT) : null;
            final int durationMs = durSec != null ? (int) Math.round(durSec * 1000.0) : 0;
            final int width = wVal != null ? wVal : 0;
            final int height = hVal != null ? hVal : 0;
            return new VideoMeta(durationMs, width, height);
        } catch (IOException | ImageProcessingException e) {
            throw new RuntimeException("Failed to read MP4 metadata for videoInfo", e);
        }
    }

    private record VideoMeta(int durationMs, int width, int height) {}
}
