package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Matrix m.audio message
 * If the filename is present, and its value is different than body, then body is considered to be a caption,
 * otherwise body is a filename. format and formatted_body are only used for captions.
*/
public class MessageAudio extends Message {
  private final String url;
  private final long durationMs;
  private final long size;
  private final String mimeType;

  protected MessageAudio(final Builder builder) {
    super(builder);
    this.url = builder.url;
    this.durationMs = builder.durationMs;
    this.size = builder.size;
    this.mimeType = builder.mimeType;
  }

  @SuppressWarnings("unused") // Constructor needed for JsonObject for sending message to homeserver // Constructor needed for JsonObject for sending message to homeserver
  @JsonCreator
  MessageAudio(
      @JsonProperty("body") final String body,
      @JsonProperty("format") final String format,
      @JsonProperty("formatted_body") final String formattedBody,
      @JsonProperty("msgtype") final String type,
      @JsonProperty("url") final String url,
      @JsonProperty("durationMs") final Long durationMs,
      @JsonProperty("size") final Long size,
      @JsonProperty("mimeType") final String mimeType) {
    super(body, format, formattedBody, type);
    this.url = url;
    this.durationMs = durationMs == null ? 0L : durationMs;
    this.size = size == null ? 0L : size;
    this.mimeType = mimeType;
  }

  @JsonProperty("url") public String url() { return url; }
  @JsonProperty("durationMs") public long durationMs() { return durationMs; }
  @JsonProperty("size") public long size() { return size; }
  @JsonProperty("mimeType") public String mimeType() { return mimeType; }

  public static final class Builder extends Message.Builder {
    private String url;
    private long durationMs;
    private long size;
    private String mimeType;

    public Builder() {
      super();
      this.type = "m.audio";
      this.size = 0L;
      this.durationMs = 0L;
    }

    public Builder url(final String url) { this.url = url; return this; }
    public Builder durationMs(final long ms) { this.durationMs = ms; return this; }
    public Builder size(final long size) { this.size = size; return this; }
    public Builder mimeType(final String mimeType) { this.mimeType = mimeType; return this; }
    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }
    @Override public MessageAudio build() { return new MessageAudio(this); }
  }
}
