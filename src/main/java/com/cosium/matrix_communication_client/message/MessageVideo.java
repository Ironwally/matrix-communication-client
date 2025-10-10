package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Matrix m.video message
 * @see <a href="https://spec.matrix.org/latest/client-server-api/#mvideo">m.video Matrix Spec</a>
 */
public class MessageVideo extends Message {

  private final String originalFilename;
  private final String url; // mxc:// URI
  private final VideoInfo info;

  protected MessageVideo(final Builder builder) {
    super(builder);
    this.originalFilename = builder.originalFilename;
    this.url = builder.url;
    this.info = builder.info;
  }

  @SuppressWarnings("unused") // Needed for JSON serialization
  @JsonCreator
  MessageVideo(
      @JsonProperty("body") final String body,
      @JsonProperty("format") final String format,
      @JsonProperty("formatted_body") final String formattedBody,
      @JsonProperty("type") final String type,
      @JsonProperty("filename") final String originalFilename,
      @JsonProperty("url") final String url,
      @JsonProperty("info") final VideoInfo info) {
    super(body, format, formattedBody, type);
    this.originalFilename = originalFilename;
    this.url = url;
    this.info = info;
  }

  @JsonProperty("filename") public String filename() { return originalFilename; }
  @JsonProperty("url") public String url() { return url; }
  @JsonProperty("info") public VideoInfo info() { return info; }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends Message.Builder {

    private String originalFilename;
    private String url;
    private VideoInfo info;

    private Builder() {
      super();
      this.type = "m.video";
      this.info = null;
      this.body = null;
    }

    // Must Override all methods otherwise returns Builder from Superclass
    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }

    public Builder caption(final String caption) { this.body = caption; return this; } // alias for body
    public Builder url(final String uri) { this.url = uri; return this; }
    public Builder videoInfo(final Integer duration, final Integer h, final String mimeType, final Integer size, final Integer w) {
      this.info = new VideoInfo(duration, h, mimeType, size, w);
      return this;
    }
    public Builder originalFilename(final String originalFilename) { this.originalFilename = originalFilename; return this; }

    @Override public MessageVideo build() {
      if (this.body == null) {
      this.body = this.originalFilename;
      }
      return new MessageVideo(this);
    }
    }

    /** Metadata about the video referred to in {@code url}.
     * @see <a href="https://spec.matrix.org/latest/client-server-api/#mvideo">m.video Matrix Spec</a>
    */
    private static class VideoInfo {
      private final Integer duration; // Duration in milliseconds
      private final Integer h;
      private final String mimeType;
      private final Integer size;  // Size in bytes
      private final Integer w;

      @JsonCreator
      public VideoInfo(
      @JsonProperty("duration") final Integer duration,
      @JsonProperty("h") final Integer h,
      @JsonProperty("mimetype") final String mimeType,
      @JsonProperty("size") final Integer size,
      @JsonProperty("w") final Integer w) {
      if (duration == null || h == null || mimeType == null || size == null || w == null) {
        throw new IllegalArgumentException("Attribute missing");
      }
      this.duration = duration;
      this.h = h;
      this.mimeType = mimeType;
      this.size = size;
      this.w = w;
      }

      @JsonProperty("duration") public Integer duration() { return duration; }
      @JsonProperty("h") public Integer height() { return h; }
      @JsonProperty("mimetype") public String mimeType() { return mimeType; }
      @JsonProperty("size") public Integer size() { return size; }
      @JsonProperty("w") public Integer width() { return w; }
    }
}
