package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Matrix m.image message
 * <a
 * href="https://spec.matrix.org/latest/client-server-api/#mimage">https://spec.matrix.org/latest/client-server-api/#mimage</a>
 */
public class MessageImage extends Message {

  private final String originalFilename;
  private final String url; // mxc:// URI
  private final ImageInfo info;

  protected MessageImage(final Builder builder) {
    super(builder);
    this.originalFilename = builder.originalFilename;
    this.url = builder.url;
    this.info = builder.info;
  }

  @SuppressWarnings("unused") // Needed for JSON serialization
  @JsonCreator
  MessageImage(
      @JsonProperty("body") final String body,
      @JsonProperty("format") final String format,
      @JsonProperty("formatted_body") final String formattedBody,
      @JsonProperty("type") final String type,
      @JsonProperty("filename") final String originalFilename,
      @JsonProperty("url") final String url,
      @JsonProperty("info") final ImageInfo info) {
    super(body, format, formattedBody, type);
    this.originalFilename = originalFilename;
    this.url = url;
    this.info = info;
  }

  @JsonProperty("filename") public String filename() { return originalFilename; }
  @JsonProperty("url") public String url() { return url; }
  @JsonProperty("info") public ImageInfo info() { return info; }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends Message.Builder {

    private String originalFilename;
    private String url;
    private ImageInfo info;

    private Builder() {
      super();
      this.type = "m.image";
      this.info = null;
      this.body = null;
    }

    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }

    public Builder caption(final String caption) { this.body = caption; return this; } // alias for body
    public Builder url(final String uri) { this.url = uri; return this; }
    public Builder imageInfo(final Integer height, final String mimeType, final Integer size, final Integer width) { this.info = new ImageInfo(height, width, size, mimeType); return this; }
    public Builder originalFilename(final String originalFilename) { this.originalFilename = originalFilename; return this; }

    @Override public MessageImage build() {
      if (this.body == null) {
        this.body = this.originalFilename;
      }
      return new MessageImage(this); }
  }

  /** Metadata about the image referred to in {@code url}.
   * <a
   * href="https://spec.matrix.org/latest/client-server-api/#mimage">https://spec.matrix.org/latest/client-server-api/#mimage</a>
  */
  private static class ImageInfo {
    private final Integer h;
    private final String mimeType;
    private final Integer size;  // Size in bytes
    private final Integer w;

    @JsonCreator
    public ImageInfo(
      @JsonProperty("h") final Integer h,
      @JsonProperty("w") final Integer w,
      @JsonProperty("size") final Integer size,
      @JsonProperty("mimetype") final String mimeType) {
      if (h == null || w == null || size == null || mimeType == null) {
        throw new IllegalArgumentException("Attribute missing");
      }
      this.h = h;
      this.mimeType = mimeType;
      this.size = size;
      this.w = w;
    }

    @JsonProperty("h") public Integer height() { return h; }
    @JsonProperty("w") public Integer width() { return w; }
    @JsonProperty("size") public Integer size() { return size; }
    @JsonProperty("mimetype") public String mimeType() { return mimeType; }
  }
}
