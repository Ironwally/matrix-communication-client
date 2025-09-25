package com.cosium.matrix_communication_client;

import com.cosium.matrix_communication_client.media.AttachmentConfig;
import com.cosium.matrix_communication_client.room.RoomResource;
import com.cosium.synapse_junit_extension.EnableSynapse;
import com.cosium.synapse_junit_extension.Synapse;
import java.io.File;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@EnableSynapse
public class MediaUploadTest {

    private MatrixResources resources;

    @BeforeEach
    void beforeEach(Synapse synapse) {
        resources = MatrixResources.factory()
            .builder()
            .https(synapse.https())
            .hostname(synapse.hostname())
            .port(synapse.port())
            .usernamePassword(synapse.adminUsername(), synapse.adminPassword())
            .build();
  } 

    @Test
    @DisplayName("Send an Image to a Room")
    void sendAttachment() {
        RoomResource room = createRoom();
        final File image = new File("cat.jpg");
        room.sendAttachment(
            "cat.jpg",
            "image/jpeg",
            image,
            AttachmentConfig.builder()
                .caption("my pretty cat")
                .build()
        );
    }

    private RoomResource createRoom() {
        CreateRoomInput createRoomInput = CreateRoomInput.builder()
                .name(UUID.randomUUID().toString())
                .roomAliasName(UUID.randomUUID().toString())
                .topic(UUID.randomUUID().toString())
                .build();
        return resources.rooms().create(createRoomInput);
    }
}
