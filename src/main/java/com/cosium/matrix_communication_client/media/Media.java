package com.media;

import java.time.Duration;
import java.util.Objects;

/** Handle to perform media-related operations on the client */
public class Media {
     // A conservative upload speed of 1Mbps
    private static final long DEFAULT_UPLOAD_SPEED_BYTES_PER_SEC = 125_000;
    // 5 min minimal upload request timeout, used to clamp the request timeout.
    private static final Duration MIN_UPLOAD_REQUEST_TIMEOUT = Duration.ofMinutes(5);

    private final Client client;

    /**
     * Media is automatically created by Client.
     * It gets an internal copy of it to use for any requests.
     */
    public Media(Client client) {
        this.client = Objects.requireNonNull(client);
    }

    public SendMediaUploadRequest upload(
        String contentType, // equivalent to &Mime. Use Mime.toString()/essence if you have a Mime type
        byte[] data,
        RequestConfig requestConfig // nullable
    ) {
        RequestConfig effectiveConfig = (requestConfig != null)
                ? requestConfig
                : client.requestConfig().withTimeout(reasonableUploadTimeout(data));
        MediaCreateContentV3Request request = new MediaCreateContentV3Request(data);
        request.setContentType(contentType);
        SendRequest<MediaCreateContentV3Request> sendRequest =
                client.send(request).withRequestConfig(effectiveConfig);
        return SendMediaUploadRequest.of(sendRequest);
    }

    private static Duration reasonableUploadTimeout(byte[] data) {
        long len = (data == null) ? 0 : data.length;
        long seconds = Math.max(
                len / DEFAULT_UPLOAD_SPEED_BYTES_PER_SEC,
                MIN_UPLOAD_REQUEST_TIMEOUT.getSeconds()
        );
        return Duration.ofSeconds(seconds);
    }

    
}

