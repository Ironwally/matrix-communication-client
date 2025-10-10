package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Matrix m.file message
 * @see <a href="https://spec.matrix.org/latest/client-server-api/#maudio">m.audio Matrix Spec</a>
*/
public class MessageAudio extends Message {
  private final String originalFilename;
  private final String url; // mxc:// URI
  private final AudioInfo info;

  protected MessageAudio(final Builder builder) {
    super(builder);
    this.originalFilename = builder.originalFilename;
    this.url = builder.url;
    this.info = builder.info;
  }

  @SuppressWarnings("unused") // Needed for JSON serialization
  @JsonCreator
  MessageAudio(
      @JsonProperty("body") final String body,
      @JsonProperty("format") final String format,
      @JsonProperty("formatted_body") final String formattedBody,
      @JsonProperty("msgtype") final String type,
      @JsonProperty("filename") final String originalFilename,
      @JsonProperty("url") final String url,
      @JsonProperty("info") final AudioInfo info) {
    super(body, format, formattedBody, type);
    this.originalFilename = originalFilename;
    this.url = url;
    this.info = info;
  }

  @JsonProperty("filename") public String filename() { return originalFilename; }
  @JsonProperty("url") public String url() { return url; }
  @JsonProperty("info") public AudioInfo info() { return info; }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends Message.Builder {
    private String originalFilename;
    private String url;
    private AudioInfo info;

    public Builder() {
      super();
      this.type = "m.audio";
      this.info = null;
      this.body = null;
    }
    // Must Override all methods otherwise returns Builder from Superclass
    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }

    public Builder caption(final String caption) { this.body = caption; return this; }
    public Builder url(final String uri) { this.url = uri; return this; }
    public Builder audioInfo(final int duration, final String mimeType, final long size) { this.info = new AudioInfo(duration, mimeType, size); return this; }
    public Builder originalFilename(final String originalFilename) { this.originalFilename = originalFilename; return this; }

    @Override public MessageAudio build() {
      if (this.body == null) {
        this.body = this.originalFilename;
      }
      return new MessageAudio(this);
    }
  }

  /** Metadata for the audio clip referred to in {@code url}. */
  protected static class AudioInfo {
    private final int duration; // Duration in milliseconds
    private final String mimeType;
    private final long size; // Size in bytes

    @JsonCreator
    public AudioInfo(
      @JsonProperty("duration") final int duration,
      @JsonProperty("mimetype") final String mimeType,
      @JsonProperty("size") final long size) {
      if (duration < 0 || mimeType == null || size == 0) {
        throw new IllegalArgumentException("Attribute missing");
      }
      this.duration = duration;
      this.mimeType = mimeType;
      this.size = size;
    }

    @JsonProperty("duration") public int duration() { return duration; }
    @JsonProperty("size") public long size() { return size; }
    @JsonProperty("mimetype") public String mimeType() { return mimeType; }
  }
}
