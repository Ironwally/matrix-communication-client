package com.cosium.matrix_communication_client.message;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/** Matrix m.file message
 * <a
 * href="https://spec.matrix.org/latest/client-server-api/#mfile">https://spec.matrix.org/latest/client-server-api/#mfile</a>
*/
public class MessageFile extends Message {
  private final String originalFilename;
  private final String url; // mxc:// URI
  private final FileInfo info;

  protected MessageFile(final Builder builder) {
    super(builder);
    this.originalFilename = builder.originalFilename;
    this.url = builder.url;
    this.info = builder.info;
  }

  @SuppressWarnings("unused") // Needed for JSON serialization
  @JsonCreator
  MessageFile(
      @JsonProperty("body") final String body,
      @JsonProperty("format") final String format,
      @JsonProperty("formatted_body") final String formattedBody,
      @JsonProperty("msgtype") final String type,
      @JsonProperty("filename") final String originalFilename,
      @JsonProperty("url") final String url,
      @JsonProperty("info") final FileInfo info) {
    super(body, format, formattedBody, type);
    this.originalFilename = originalFilename;
    this.url = url;
    this.info = info;
  }

  @JsonProperty("filename") public String filename() { return originalFilename; }
  @JsonProperty("url") public String url() { return url; }
  @JsonProperty("info") public FileInfo info() { return info; }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends Message.Builder {
    private String originalFilename;
    private String url;
    private FileInfo info;

    public Builder() {
      super();
      this.type = "m.file";
      this.info = null;
      this.body = null;
    }

    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }


    public Builder caption(final String caption) { this.body = caption; return this; }
    public Builder url(final String uri) { this.url = uri; return this; }
    public Builder fileInfo(final String mimeType, final long size) { this.info = new FileInfo(mimeType, size); return this; }
    public Builder originalFilename(final String originalFilename) { this.originalFilename = originalFilename; return this; }

    @Override public MessageFile build() {
      if (this.body == null) {
        this.body = this.originalFilename;
      }
      return new MessageFile(this);
    }
  }

  /** Information about the file referred to in {@code url}. */
  protected static class FileInfo {
    private final String mimeType;
    private final long size; // Size in bytes

    @JsonCreator
    public FileInfo(
      @JsonProperty("mimetype") final String mimeType,
      @JsonProperty("size") final long size) {
      if (size == 0 || mimeType == null) {
        throw new IllegalArgumentException("Attribute missing");
      }
      this.mimeType = mimeType;
      this.size = size;
    }

    @JsonProperty("size") public long size() { return size; }
    @JsonProperty("mimetype") public String mimeType() { return mimeType; }
  }
}
