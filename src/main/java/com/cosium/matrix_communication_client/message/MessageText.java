package com.cosium.matrix_communication_client.message;

/** Matrix m.text message */
public class MessageText extends Message {
  protected MessageText(final Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder extends Message.Builder {

    Builder() {
      super();
      this.type = "m.text";
    }

    public Builder text(final String text) { this.body = text; return this; }

    @Override public Builder body(final String body) { this.body = body; return this; }
    @Override public Builder format(final String format) { this.format = format; return this; }
    @Override public Builder formattedBody(final String formattedBody) { this.formattedBody = formattedBody; return this; }
    @Override public MessageText build() { return new MessageText(this); }
  }
}
