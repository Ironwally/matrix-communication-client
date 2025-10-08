package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Matrix m.file message
 * If the filename is present, and its value is different than body, then body is considered to be a caption,
 * otherwise body is a filename. format and formatted_body are only used for captions.
*/
public class MessageFile extends Message {
  private final String url;
  private final long size;
  private final String mimeType;

  protected MessageFile(Builder builder) {
    super(builder);
    this.url = builder.url;
    this.size = builder.size;
    this.mimeType = builder.mimeType;
  }

  @SuppressWarnings("unused") // Constructor needed for JsonObject for sending message to homeserver // Constructor needed for JsonObject for sending message to homeserver
  @JsonCreator
  MessageFile(
      @JsonProperty("body") String body,
      @JsonProperty("format") String format,
      @JsonProperty("formatted_body") String formattedBody,
      @JsonProperty("msgtype") String type,
      @JsonProperty("url") String url,
      @JsonProperty("size") Long size,
      @JsonProperty("mimeType") String mimeType) {
    super(body, format, formattedBody, type);
    this.url = url;
    this.size = size == null ? 0L : size;
    this.mimeType = mimeType;
  }

  @JsonProperty("url") public String url() { return url; }
  @JsonProperty("size") public long size() { return size; }
  @JsonProperty("mimeType") public String mimeType() { return mimeType; }

  public static final class Builder extends Message.Builder {
    private String url;
    private long size;
    private String mimeType;

    public Builder(Message.Builder base) {
      super(base);
      this.type = "m.file";
      this.size = 0L;
    }

    public Builder url(String url) { this.url = url; return this; }
    public Builder size(long size) { this.size = size; return this; }
    public Builder mimeType(String mimeType) { this.mimeType = mimeType; return this; }
    @Override public Builder body(String body) { this.body = body; return this; }
    @Override public Builder format(String format) { this.format = format; return this; }
    @Override public Builder formattedBody(String formattedBody) { this.formattedBody = formattedBody; return this; }
    @Override public MessageFile build() { return new MessageFile(this); }
  }
}
