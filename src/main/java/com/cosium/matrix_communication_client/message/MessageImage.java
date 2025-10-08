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
      @JsonProperty("filename") String filename,
      @JsonProperty("url") String url,
      @JsonProperty("info") ImageInfo info) {
    super(body, format, formattedBody, type);
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
      this.info = null;
    }

    @Override public Builder body(String body) { this.body = body; return this; }
    @Override public Builder format(String format) { this.format = format; return this; }
    @Override public Builder formattedBody(String formattedBody) { this.formattedBody = formattedBody; return this; }

    public Builder caption(String caption) { this.body = caption; return this; } // alias for body
    public Builder url(String uri) { this.url = uri; return this; }
    public Builder imageInfo(Integer height, String mimeType, Integer size, Integer width) { this.info = new ImageInfo(height, width, size, mimeType); return this; }
    public Builder filename(String filename) { this.filename = filename; return this; }

    @Override public MessageImage build() { return new MessageImage(this); }
  }

  protected static class ImageInfo {
    private final Integer h;
    private final String mimeType;
    private final Integer size;
    private final Integer w;

    @JsonCreator
    public ImageInfo(
      @JsonProperty("h") Integer h,
      @JsonProperty("w") Integer w,
      @JsonProperty("size") Integer size,
      @JsonProperty("mimetype") String mimeType) {
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
