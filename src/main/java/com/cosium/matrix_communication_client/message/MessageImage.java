package com.cosium.matrix_communication_client.message;

/** Matrix m.image message */
public class MessageImage extends Message {

  private final String url; // mxc:// URI or http fallback
  private final String filename;
  private final ImageInfo info;

  protected MessageImage(Builder builder) {
    super(builder);
    this.url = builder.url;
    this.filename = builder.filename;
    this.info = builder.info;
  }

  public String url() {
    return url;
  }

  public String filename() {
    return filename;
  }

  public ImageInfo info() {
    return info;
  }

  public static final class Builder extends Message.Builder {

    private String url;
    private String filename;
    private ImageInfo info;

    Builder(Message.Builder base) {
      super(base);
      this.type = "m.image";
    }

    public Builder url(String uri) {
      this.url = uri;
      return this;
    }

    public Builder fillMetaData() {
      // TODO: Requires implementing matrix media API resolver
      throw new UnsupportedOperationException("Not implemented yet.");
    }

    // caption for image
    @Override
    public Builder body(String body) {
      super.body(body);
      return this;
    }

    @Override
    public Builder format(String format) {
      super.format(format);
      return this;
    }

    @Override
    public Builder formattedBody(String formattedBody) {
      super.formattedBody(formattedBody);
      return this;
    }

    @Override
    public Builder timestamp(long timestamp) {
      super.timestamp(timestamp);
      return this;
    }

    @Override
    public Builder id(long id) {
      super.id(id);
      return this;
    }

    @Override
    public MessageImage build() {
      return new MessageImage(this);
    }
  }

  public static class ImageInfo {
    public final Integer h;
    public final Integer w;
    public final Integer size;
    public final String mimeType;

    public ImageInfo(Integer h, Integer w, Integer size, String mimeType) {
      if (h == null || w == null || size == null || mimeType == null) {
        throw new IllegalArgumentException("Attribute missing");
      }
      this.h = h;
      this.w = w;
      this.size = size;
      this.mimeType = mimeType;
    }
  }
}
