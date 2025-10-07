package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Matrix m.image message
 * If the filename is present, and its value is different than body, then body is considered to be a caption,
 * otherwise body is a filename. format and formatted_body are only used for captions.
*/
public class MessageImage extends Message {

  private final String filename; // original filename
  private final String url; // mxc:// URI
  private final ImageInfo info;

  protected MessageImage(Builder builder) {
    super(builder);
    this.filename = builder.filename;
    this.url = builder.url;
    this.info = builder.info;
  }

  @SuppressWarnings("unused") // Constructor needed for JsonObject for sending message to homeserver
  @JsonCreator
  MessageImage(
      @JsonProperty("body") String body,
      @JsonProperty("format") String format,
      @JsonProperty("formatted_body") String formattedBody,
      @JsonProperty("type") String type,
      @JsonProperty("timestamp") Long timestamp,
      @JsonProperty("id") Long id,
      @JsonProperty("filename") String filename,
      @JsonProperty("url") String url,
      @JsonProperty("info") ImageInfo info) {
    super(body, format, formattedBody, type, timestamp, id);
    this.filename = filename;
    this.url = url;
    this.info = info;
  }

  @JsonProperty("filename") public String filename() { return filename; }
  @JsonProperty("url") public String url() { return url; }
  @JsonProperty("info") public ImageInfo info() { return info; }

  public static final class Builder extends Message.Builder {

    private String filename;
    private String url;
    private ImageInfo info;

    Builder(Message.Builder base) {
      super(base);
      this.type = "m.image";
    }

    public Builder url(String uri) { this.url = uri; return this; }

    /** Fetches the metadata from the matrix media repository **/
    public Builder fetchMetaData() {
      // TODO: Requires implementing matrix media API resolver
      return this;
    }

    // caption for image
    @Override public Builder body(String body) { super.body(body); return this; }
    @Override public Builder format(String format) { super.format(format); return this; }
    @Override public Builder formattedBody(String formattedBody) { super.formattedBody(formattedBody); return this; }
    @Override public Builder timestamp(long timestamp) { super.timestamp(timestamp); return this; }
    @Override public Builder id(long id) { super.id(id); return this; }
    @Override public MessageImage build() { return new MessageImage(this); }
  }

  public static class ImageInfo {
    public final Integer h;
    public final String mimeType;
    public final Integer size;
    public final Integer w;

    public ImageInfo(Integer h, Integer w, Integer size, String mimeType) {
      if (h == null || w == null || size == null || mimeType == null) {
        throw new IllegalArgumentException("Attribute missing");
      }
      this.h = h;
      this.mimeType = mimeType;
      this.size = size;
      this.w = w;
    }
  }
}
