package com.cosium.matrix_communication_client.message;

/** Matrix m.text message */
public class MessageText extends Message {
  protected MessageText(Builder builder) {
    super(builder);
  }

  public static final class Builder extends Message.Builder {

    Builder(Message.Builder base) {
      super(base);
      this.type = "m.text";
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
    public MessageText build() {
      return new MessageText(this);
    }
  }
}
