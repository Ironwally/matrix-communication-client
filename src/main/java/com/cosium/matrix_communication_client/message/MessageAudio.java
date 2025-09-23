package com.cosium.matrix_communication_client.message;

/** Matrix m.audio message */
public class MessageAudio extends Message {
  private final String url;
  private final long durationMs;
  private final long size;
  private final String mimeType;

  protected MessageAudio(Builder builder) {
    super(builder);
    this.url = builder.url;
    this.durationMs = builder.durationMs;
    this.size = builder.size;
    this.mimeType = builder.mimeType;
  }

  public String url() {
    return url;
  }

  public long durationMs() {
    return durationMs;
  }

  public long size() {
    return size;
  }

  public String mimeType() {
    return mimeType;
  }

  public static final class Builder extends Message.Builder {
    private String url;
    private long durationMs;
    private long size;
    private String mimeType;

    public Builder(Message.Builder base) {
      super(base);
      this.type = "m.audio";
      this.size = 0L;
      this.durationMs = 0L;
    }

    public Builder url(String url) {
      this.url = url;
      return this;
    }

    public Builder durationMs(long ms) {
      this.durationMs = ms;
      return this;
    }

    public Builder size(long size) {
      this.size = size;
      return this;
    }

    public Builder mimeType(String mimeType) {
      this.mimeType = mimeType;
      return this;
    }

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
    public MessageAudio build() {
      return new MessageAudio(this);
    }
  }
}
