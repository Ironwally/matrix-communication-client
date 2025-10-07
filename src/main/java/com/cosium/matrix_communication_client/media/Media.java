package com.cosium.matrix_communication_client.media;

import com.cosium.matrix_communication_client.MatrixApi;
import java.io.File;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Objects;

/** Handle to perform media-related operations on the client */
public class Media implements MediaResource {
     // A conservative upload speed of 1Mbps
    private static final long DEFAULT_UPLOAD_SPEED_BYTES_PER_SEC = 125_000;
    // 5 min minimal upload request timeout, used to clamp the request timeout.
    private static final Duration MIN_UPLOAD_REQUEST_TIMEOUT = Duration.ofMinutes(5);

    // Client for use to send requests
    public MatrixApi client;
    /**
     * Media is automatically created by Client.
     * It gets an internal copy of it to use for any requests.
     */
    public Media(MatrixApi client) {
        this.client = Objects.requireNonNull(client);
    }

    @Override
    public String upload(File file, AttachmentConfig config) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(config);
        String contentType = config.getContentType();
        if (contentType.isEmpty()) { contentType = "application/octet-stream"; }

        byte[] data;
        try {
            data = Files.readAllBytes(file.toPath());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read file " + file.getAbsolutePath(), e);
        }

    return client.uploadMedia(
        file.getName(),
        contentType,
        data
    ).contentUri();
    }

    @Override
    public byte[] download(String mxcUri) {
        Objects.requireNonNull(mxcUri);
        return client.downloadMedia(mxcUri);
    }
}

